package com.adm.lucas.posts.core.domain;

import java.util.UUID;

public class Upvote {

    private UUID id = UUID.randomUUID();
    private User user;
    private Post post;

    public Upvote() {
    }

    public Upvote(User user, Post post) {
        this.user = user;
        this.post = post;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}