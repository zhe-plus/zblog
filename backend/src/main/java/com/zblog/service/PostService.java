package com.zblog.service;

import com.zblog.common.SlugUtils;
import com.zblog.dto.request.CreatePostRequest;
import com.zblog.dto.response.Pagination;
import com.zblog.dto.response.PostDetailResponse;
import com.zblog.dto.response.PostSummaryResponse;
import com.zblog.entity.*;
import com.zblog.enums.ErrorCode;
import com.zblog.enums.PostStatus;
import com.zblog.exception.BadRequestException;
import com.zblog.exception.ResourceNotFoundException;
import com.zblog.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class PostService {

    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final PostMediaRepository postMediaRepository;
    private final MediaRepository mediaRepository;
    private final MarkdownService markdownService;
    private final SlugService slugService;
    private final CacheService cacheService;

    private static final Pattern IMG_PATTERN = Pattern.compile("<img[^>]+src=\"(/uploads/[^\"]+)\"[^>]*>");

    public PostService(PostRepository postRepository, CategoryRepository categoryRepository,
                       TagRepository tagRepository, PostMediaRepository postMediaRepository,
                       MediaRepository mediaRepository, MarkdownService markdownService,
                       SlugService slugService, CacheService cacheService) {
        this.postRepository = postRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.postMediaRepository = postMediaRepository;
        this.mediaRepository = mediaRepository;
        this.markdownService = markdownService;
        this.slugService = slugService;
        this.cacheService = cacheService;
    }

    // === Public ===

    public Page<PostSummaryResponse> getPublicPosts(Long categoryId, Long tagId, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "isPinned", "publishedAt"));
        return postRepository.findPublicPosts(PostStatus.PUBLIC, categoryId, tagId, pageable)
            .map(this::toSummary);
    }

    public PostDetailResponse getPublicPostBySlug(String slug) {
        Post post = postRepository.findBySlugAndDeletedAtIsNull(slug)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.POST_NOT_FOUND));
        if (post.getStatus() == PostStatus.DRAFT) {
            throw new ResourceNotFoundException(ErrorCode.POST_NOT_FOUND);
        }
        if (post.getStatus() != PostStatus.PUBLIC) {
            throw new ResourceNotFoundException(ErrorCode.POST_NOT_FOUND);
        }
        return toDetail(post);
    }

    public List<Map<String, Object>> getArchive() {
        List<Post> posts = postRepository.findArchivePosts(PostStatus.PUBLIC);
        Map<Integer, Map<Integer, List<Map<String, Object>>>> yearMonth = new LinkedHashMap<>();
        for (Post p : posts) {
            if (p.getPublishedAt() == null) continue;
            int year = p.getPublishedAt().getYear();
            int month = p.getPublishedAt().getMonthValue();
            yearMonth.computeIfAbsent(year, k -> new LinkedHashMap<>());
            yearMonth.get(year).computeIfAbsent(month, k -> new ArrayList<>());
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("title", p.getTitle());
            item.put("slug", p.getSlug());
            item.put("published_at", p.getPublishedAt().toString());
            yearMonth.get(year).get(month).add(item);
        }
        List<Map<String, Object>> result = new ArrayList<>();
        yearMonth.forEach((year, months) -> {
            Map<String, Object> ym = new LinkedHashMap<>();
            ym.put("year", year);
            List<Map<String, Object>> monthsList = new ArrayList<>();
            months.forEach((month, postsList) -> {
                Map<String, Object> m = new LinkedHashMap<>();
                m.put("month", month);
                m.put("posts", postsList);
                monthsList.add(m);
            });
            ym.put("months", monthsList);
            result.add(ym);
        });
        return result;
    }

    // === Admin ===

    public Page<PostSummaryResponse> getAllPosts(String status, int page, int pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize, Sort.by(Sort.Direction.DESC, "updatedAt"));
        Page<Post> posts;
        if (status != null && !status.isBlank()) {
            posts = postRepository.findAllActiveByStatus(PostStatus.fromValue(status), pageable);
        } else {
            posts = postRepository.findAllActive(pageable);
        }
        return posts.map(this::toSummary);
    }

    public PostDetailResponse getPostById(Long id) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.POST_NOT_FOUND));
        return toDetail(post);
    }

    @Transactional
    public PostDetailResponse createPost(CreatePostRequest request) {
        if (request.getTitle() == null || request.getTitle().isBlank()) {
            throw new BadRequestException(ErrorCode.POST_TITLE_EMPTY);
        }

        String slug = request.getSlug();
        if (slug == null || slug.isBlank()) {
            slug = slugService.generate(request.getTitle());
        }
        slug = slugService.ensureUnique(slug, postRepository::existsBySlugAndDeletedAtIsNull);

        if (SlugUtils.isReserved(slug)) {
            throw new BadRequestException(ErrorCode.SLUG_RESERVED);
        }

        Post post = new Post();
        post.setTitle(request.getTitle());
        post.setSlug(slug);
        post.setContent(request.getContent() != null ? request.getContent() : "");
        post.setContentHtml(markdownService.renderToHtml(post.getContent()));
        post.setSummary(request.getSummary());

        PostStatus status = request.getStatus() != null ? PostStatus.fromValue(request.getStatus()) : PostStatus.DRAFT;
        post.setStatus(status);
        if (status == PostStatus.PUBLIC || status == PostStatus.PRIVATE) {
            post.setPublishedAt(LocalDateTime.now());
        }

        if (request.getScheduledAt() != null) {
            post.setScheduledAt(request.getScheduledAt());
        }

        setCategory(post, request.getCategoryId());
        setTags(post, request.getTagIds());

        post = postRepository.save(post);
        updatePostMedia(post);
        return toDetail(post);
    }

    @Transactional
    public PostDetailResponse updatePost(Long id, CreatePostRequest request) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.POST_NOT_FOUND));

        if (request.getTitle() != null) {
            post.setTitle(request.getTitle());
        }
        if (request.getSlug() != null && !request.getSlug().isBlank()) {
            if (!request.getSlug().equals(post.getSlug()) && postRepository.existsBySlugAndDeletedAtIsNull(request.getSlug())) {
                throw new BadRequestException(ErrorCode.POST_SLUG_DUPLICATE);
            }
            post.setSlug(request.getSlug());
        }
        if (request.getContent() != null) {
            post.setContent(request.getContent());
            post.setContentHtml(markdownService.renderToHtml(request.getContent(), post.getId()));
        }
        if (request.getSummary() != null) {
            post.setSummary(request.getSummary());
        }
        if (request.getStatus() != null) {
            PostStatus newStatus = PostStatus.fromValue(request.getStatus());
            post.setStatus(newStatus);
            if ((newStatus == PostStatus.PUBLIC || newStatus == PostStatus.PRIVATE) && post.getPublishedAt() == null) {
                post.setPublishedAt(LocalDateTime.now());
            }
        }
        if (request.getCategoryId() != null) {
            setCategory(post, request.getCategoryId());
        }
        if (request.getTagIds() != null) {
            setTags(post, request.getTagIds());
        }
        if (request.getScheduledAt() != null) {
            post.setScheduledAt(request.getScheduledAt());
        }

        post = postRepository.save(post);
        updatePostMedia(post);

        // Clear cache
        String catSlug = post.getCategory() != null ? post.getCategory().getSlug() : null;
        Set<String> tagSlugs = post.getTags().stream().map(Tag::getSlug).collect(Collectors.toSet());
        cacheService.evictPostRelated(post.getSlug(), catSlug, tagSlugs);

        return toDetail(post);
    }

    @Transactional
    public void deletePost(Long id) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.POST_NOT_FOUND));
        post.setDeletedAt(LocalDateTime.now());
        postRepository.save(post);

        String catSlug = post.getCategory() != null ? post.getCategory().getSlug() : null;
        Set<String> tagSlugs = post.getTags().stream().map(Tag::getSlug).collect(Collectors.toSet());
        cacheService.evictPostRelated(post.getSlug(), catSlug, tagSlugs);
    }

    @Transactional
    public PostDetailResponse changeStatus(Long id, String status) {
        Post post = postRepository.findByIdAndDeletedAtIsNull(id)
            .orElseThrow(() -> new ResourceNotFoundException(ErrorCode.POST_NOT_FOUND));
        PostStatus newStatus = PostStatus.fromValue(status);
        post.setStatus(newStatus);
        if ((newStatus == PostStatus.PUBLIC || newStatus == PostStatus.PRIVATE) && post.getPublishedAt() == null) {
            post.setPublishedAt(LocalDateTime.now());
        }
        post = postRepository.save(post);

        String catSlug = post.getCategory() != null ? post.getCategory().getSlug() : null;
        Set<String> tagSlugs = post.getTags().stream().map(Tag::getSlug).collect(Collectors.toSet());
        cacheService.evictPostRelated(post.getSlug(), catSlug, tagSlugs);

        return toDetail(post);
    }

    // === Private Helpers ===

    private void setCategory(Post post, Long categoryId) {
        if (categoryId != null) {
            Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new BadRequestException(ErrorCode.CATEGORY_NOT_FOUND));
            post.setCategory(category);
        } else {
            post.setCategory(null);
        }
    }

    private void setTags(Post post, List<Long> tagIds) {
        post.getTags().clear();
        if (tagIds != null && !tagIds.isEmpty()) {
            List<Tag> tags = tagRepository.findAllById(tagIds);
            post.getTags().addAll(tags);
        }
    }

    private void updatePostMedia(Post post) {
        postMediaRepository.deleteByPostId(post.getId());
        Matcher m = IMG_PATTERN.matcher(post.getContentHtml());
        while (m.find()) {
            String path = m.group(1);
            List<Media> mediaList = mediaRepository.findByStoragePathIn(List.of(path));
            for (Media media : mediaList) {
                PostMedia pm = new PostMedia(post, media);
                postMediaRepository.save(pm);
            }
        }
    }

    private PostSummaryResponse toSummary(Post p) {
        PostSummaryResponse r = new PostSummaryResponse();
        r.setId(p.getId());
        r.setTitle(p.getTitle());
        r.setSlug(p.getSlug());
        r.setSummary(p.getSummary() != null ? p.getSummary() : markdownService.generateSummary(p.getContent(), 160));
        r.setIsPinned(p.getIsPinned());
        r.setPublishedAt(p.getPublishedAt());
        r.setReadingTime(null);
        if (p.getCategory() != null) {
            PostSummaryResponse.CategoryInfo ci = new PostSummaryResponse.CategoryInfo();
            ci.setId(p.getCategory().getId());
            ci.setName(p.getCategory().getName());
            ci.setSlug(p.getCategory().getSlug());
            r.setCategory(ci);
        }
        List<PostSummaryResponse.TagInfo> tagInfos = p.getTags().stream().map(t -> {
            PostSummaryResponse.TagInfo ti = new PostSummaryResponse.TagInfo();
            ti.setId(t.getId());
            ti.setName(t.getName());
            ti.setSlug(t.getSlug());
            return ti;
        }).collect(Collectors.toList());
        r.setTags(tagInfos);
        return r;
    }

    private PostDetailResponse toDetail(Post p) {
        PostDetailResponse r = new PostDetailResponse();
        r.setId(p.getId());
        r.setTitle(p.getTitle());
        r.setSlug(p.getSlug());
        r.setContent(p.getContent());
        r.setContentHtml(p.getContentHtml());
        r.setSummary(p.getSummary());
        r.setIsPinned(p.getIsPinned());
        r.setPublishedAt(p.getPublishedAt());
        r.setUpdatedAt(p.getUpdatedAt());
        r.setReadingTime(null);
        r.setPrevPost(null);
        r.setNextPost(null);
        r.setBacklinks(List.of());
        if (p.getCategory() != null) {
            PostSummaryResponse.CategoryInfo ci = new PostSummaryResponse.CategoryInfo();
            ci.setId(p.getCategory().getId());
            ci.setName(p.getCategory().getName());
            ci.setSlug(p.getCategory().getSlug());
            r.setCategory(ci);
        }
        List<PostSummaryResponse.TagInfo> tagInfos = p.getTags().stream().map(t -> {
            PostSummaryResponse.TagInfo ti = new PostSummaryResponse.TagInfo();
            ti.setId(t.getId());
            ti.setName(t.getName());
            ti.setSlug(t.getSlug());
            return ti;
        }).collect(Collectors.toList());
        r.setTags(tagInfos);
        return r;
    }
}
