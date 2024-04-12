package com.adm.lucas.posts.core.domain;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.UUID;

public class Answer {

    private UUID id = UUID.randomUUID();
    private User user;
    private Comment comment;
    private LocalDateTime dateAnswer = LocalDateTime.now(ZoneId.of("America/Sao_Paulo"));
    private String text;

    public Answer() {
    }

    public Answer(User user, Comment comment, String text) {
        this.user = user;
        this.comment = comment;
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

    public Comment getComment() {
        return comment;
    }

    public void setComment(Comment comment) {
        this.comment = comment;
    }

    public LocalDateTime getDateAnswer() {
        return dateAnswer;
    }

    public void setDateAnswer(LocalDateTime dateAnswer) {
        this.dateAnswer = dateAnswer;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

}