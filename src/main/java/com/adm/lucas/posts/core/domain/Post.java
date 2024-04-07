package com.adm.lucas.posts.core.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class Post {

    private UUID id = UUID.randomUUID();
    private User user;
    private String username;
    private String text;
    private LocalDateTime datePost = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    private Status status = Status.OPEN;
//    private List<Upvote> upvotes = new ArrayList<>();
//    private List<Comment> comments = new ArrayList<>();

    public Post() {
    }

    public Post(User user, String text) {
        this.user = user;
        this.text = text;
        this.username = user.getUsername();
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

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

//    public List<Upvote> getUpvotes() {
//        return upvotes;
//    }

//    public void setUpvotes(List<Upvote> upvotes) {
//        this.upvotes = upvotes;
//    }

//    public List<Comment> getComments() {
//        return comments;
//    }

//    public void setComments(List<Comment> comments) {
//        this.comments = comments;
//    }

}