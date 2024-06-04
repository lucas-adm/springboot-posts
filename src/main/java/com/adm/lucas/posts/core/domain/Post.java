package com.adm.lucas.posts.core.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.UUID;

public class Post {

    private UUID id = UUID.randomUUID();
    private User user;
    private String text;
    private LocalDateTime datePost = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    private Status status = Status.OPEN;
    private Set<Upvote> upvotes;
    private Set<Comment> comments;
    private int upvoteCount = 0;
    private int commentCount = 0;

    public Post() {
    }

    public Post(User user, String text) {
        this.user = user;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public LocalDateTime getDatePost() {
        return datePost;
    }

    public void setDatePost(LocalDateTime datePost) {
        this.datePost = datePost;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Set<Upvote> getUpvotes() {
        return upvotes;
    }

    public void setUpvotes(Set<Upvote> upvotes) {
        this.upvotes = upvotes;
    }

    public Set<Comment> getComments() {
        return comments;
    }

    public void setComments(Set<Comment> comments) {
        this.comments = comments;
    }

    public int getUpvoteCount() {
        return upvoteCount;
    }

    public void setUpvoteCount(int upvoteCount) {
        this.upvoteCount = upvoteCount;
    }

    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

}