package com.zblog.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class PostMediaId implements Serializable {

    @Column(name = "post_id")
    private Long postId;

    @Column(name = "media_id")
    private Long mediaId;

    public PostMediaId() {}

    public PostMediaId(Long postId, Long mediaId) {
        this.postId = postId;
        this.mediaId = mediaId;
    }

    public Long getPostId() { return postId; }
    public void setPostId(Long postId) { this.postId = postId; }
    public Long getMediaId() { return mediaId; }
    public void setMediaId(Long mediaId) { this.mediaId = mediaId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PostMediaId that = (PostMediaId) o;
        return Objects.equals(postId, that.postId) && Objects.equals(mediaId, that.mediaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(postId, mediaId);
    }
}
