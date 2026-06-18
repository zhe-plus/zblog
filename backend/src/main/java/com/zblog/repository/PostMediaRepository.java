package com.zblog.repository;

import com.zblog.entity.PostMedia;
import com.zblog.entity.PostMediaId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostMediaRepository extends JpaRepository<PostMedia, PostMediaId> {

    void deleteByPostId(Long postId);

    @Query("SELECT pm FROM PostMedia pm WHERE pm.post.id = :postId")
    List<PostMedia> findByPostId(@Param("postId") Long postId);

    @Query("SELECT COUNT(pm) > 0 FROM PostMedia pm JOIN Post p ON pm.post.id = p.id " +
           "WHERE pm.media.id = :mediaId AND p.deletedAt IS NULL")
    boolean isMediaReferencedByActivePost(@Param("mediaId") Long mediaId);
}
