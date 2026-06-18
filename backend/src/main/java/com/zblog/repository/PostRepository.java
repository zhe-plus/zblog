package com.zblog.repository;

import com.zblog.entity.Post;
import com.zblog.enums.PostStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    Optional<Post> findBySlugAndDeletedAtIsNull(String slug);

    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.deletedAt IS NULL " +
           "AND (:categoryId IS NULL OR p.category.id = :categoryId) " +
           "AND (:tagId IS NULL OR :tagId IN (SELECT t.id FROM p.tags t))")
    Page<Post> findPublicPosts(@Param("status") PostStatus status,
                                @Param("categoryId") Long categoryId,
                                @Param("tagId") Long tagId,
                                Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL")
    Page<Post> findAllActive(Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.deletedAt IS NULL AND p.status = :status")
    Page<Post> findAllActiveByStatus(@Param("status") PostStatus status, Pageable pageable);

    List<Post> findBySlugInAndStatusAndDeletedAtIsNull(Collection<String> slugs, PostStatus status);

    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.deletedAt IS NULL AND " +
           "p.publishedAt IS NOT NULL ORDER BY p.publishedAt DESC")
    List<Post> findPublishedPosts(@Param("status") PostStatus status);

    @Query("SELECT p FROM Post p WHERE p.status = :status AND p.deletedAt IS NULL " +
           "AND p.publishedAt IS NOT NULL ORDER BY p.publishedAt DESC")
    List<Post> findArchivePosts(@Param("status") PostStatus status);

    boolean existsBySlugAndDeletedAtIsNull(String slug);

    Optional<Post> findByIdAndDeletedAtIsNull(Long id);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLIC' AND p.deletedAt IS NULL " +
           "AND p.publishedAt IS NOT NULL AND p.publishedAt < :publishedAt " +
           "ORDER BY p.publishedAt DESC")
    List<Post> findPrevPost(@Param("publishedAt") java.time.LocalDateTime publishedAt, Pageable pageable);

    @Query("SELECT p FROM Post p WHERE p.status = 'PUBLIC' AND p.deletedAt IS NULL " +
           "AND p.publishedAt IS NOT NULL AND p.publishedAt > :publishedAt " +
           "ORDER BY p.publishedAt ASC")
    List<Post> findNextPost(@Param("publishedAt") java.time.LocalDateTime publishedAt, Pageable pageable);
}
