package com.zblog.repository;

import com.zblog.entity.Tag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TagRepository extends JpaRepository<Tag, Long> {

    Optional<Tag> findBySlug(String slug);

    boolean existsBySlug(String slug);

    @Query("SELECT t, COUNT(p) FROM Tag t LEFT JOIN t.posts p WHERE p.status = 'PUBLIC' AND p.deletedAt IS NULL GROUP BY t")
    List<Object[]> findAllWithPostCount();
}
