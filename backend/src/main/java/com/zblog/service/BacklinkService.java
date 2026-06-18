package com.zblog.service;

import com.zblog.entity.Post;
import com.zblog.enums.PostStatus;
import com.zblog.repository.PostRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class BacklinkService {

    private static final Pattern WIKI_LINK = Pattern.compile("\\[\\[([^\\]]+)\\]\\]");
    private final PostRepository postRepository;

    public BacklinkService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    public String resolveWikiLinks(String markdown, Long currentPostId) {
        Matcher m = WIKI_LINK.matcher(markdown);

        Set<String> slugs = new HashSet<>();
        while (m.find()) {
            slugs.add(m.group(1).trim());
        }

        Map<String, String> slugTitleMap;
        if (!slugs.isEmpty()) {
            slugTitleMap = postRepository
                .findBySlugInAndStatusAndDeletedAtIsNull(slugs, PostStatus.PUBLIC)
                .stream()
                .collect(Collectors.toMap(Post::getSlug, Post::getTitle));
        } else {
            slugTitleMap = Map.of();
        }

        m.reset();
        StringBuffer sb = new StringBuffer();
        while (m.find()) {
            String slug = m.group(1).trim();
            String title = slugTitleMap.getOrDefault(slug, slug);
            String replacement = String.format("[%s](/post/%s)", title, slug);
            m.appendReplacement(sb, Matcher.quoteReplacement(replacement));
        }
        m.appendTail(sb);
        return sb.toString();
    }
}
