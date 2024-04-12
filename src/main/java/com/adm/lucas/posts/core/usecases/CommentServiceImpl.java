package com.adm.lucas.posts.core.usecases;

import com.adm.lucas.posts.core.domain.Comment;
import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.domain.Status;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.repositories.CommentRepositoryPort;
import com.adm.lucas.posts.core.ports.services.CommentServicePort;

import java.util.Objects;
import java.util.UUID;

public class CommentServiceImpl implements CommentServicePort {

    private final CommentRepositoryPort repositoryPort;

    public CommentServiceImpl(CommentRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public void comment(String username, UUID uuid, String text) {
        User user = repositoryPort.findUserByUsername(username);
        Post post = repositoryPort.findPostById(uuid);
        if (post.getStatus() == Status.CLOSED) {
            throw new RuntimeException("This post is closed.");
        }
        repositoryPort.saveComment(new Comment(user, post, text));
    }

    @Override
    public void edit(String username, UUID uuid, String text) {
        Comment comment = repositoryPort.findCommentById(uuid);
        if (!Objects.equals(comment.getUser().getUsername(), username)) {
            throw new RuntimeException("Only the comment user can edit this comment.");
        }
        comment.setText(text);
        repositoryPort.saveComment(comment);
    }

    @Override
    public void remove(String username, UUID uuid) {
        Comment comment = repositoryPort.findCommentById(uuid);
        if (!Objects.equals(comment.getUser().getUsername(), username)) {
            throw new RuntimeException("Only the comment user can edit this comment.");
        }
        repositoryPort.removeComment(comment);
    }

}