package com.zblog.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "post_media")
public class PostMedia {

    @EmbeddedId
    private PostMediaId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("postId")
    @JoinColumn(name = "post_id")
    private Post post;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("mediaId")
    @JoinColumn(name = "media_id")
    private Media media;

    public PostMedia() {}

    public PostMedia(Post post, Media media) {
        this.post = post;
        this.media = media;
        this.id = new PostMediaId(post.getId(), media.getId());
    }

    public PostMediaId getId() { return id; }
    public void setId(PostMediaId id) { this.id = id; }
    public Post getPost() { return post; }
    public void setPost(Post post) { this.post = post; }
    public Media getMedia() { return media; }
    public void setMedia(Media media) { this.media = media; }
}
