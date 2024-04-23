package com.adm.lucas.posts.core.ports.services;

import com.adm.lucas.posts.core.domain.Comment;

import java.util.UUID;

public interface CommentServicePort {

    Comment comment(String username, UUID uuid, String text);

    void edit(String username, UUID uuid, String text);

    void remove(String username, UUID uuid);

}