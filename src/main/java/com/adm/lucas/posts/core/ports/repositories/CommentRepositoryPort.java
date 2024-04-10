package com.adm.lucas.posts.core.ports.repositories;

import com.adm.lucas.posts.core.domain.Comment;
import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.domain.User;

import java.util.UUID;

public interface CommentRepositoryPort {

    User findUserByUsername(String username);

    Post findPostById(UUID uuid);

    Comment findCommentById(UUID uuid);

    void saveComment(Comment comment);

    void removeComment(Comment comment);

}