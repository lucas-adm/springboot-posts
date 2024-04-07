package com.adm.lucas.posts.core.ports.services;

import com.adm.lucas.posts.core.domain.Post;

import java.util.List;
import java.util.UUID;

public interface PostServicePort {

    void create(String username, String text);

    void edit(UUID uuid, String username, String text);

    void close(UUID uuid, String username);

    void delete(UUID uuid, String username);

    Post select(UUID uuid);

    List<Post> listPosts(int page, int size, String sortBy, String sortOrder);

    List<Post> listUserPosts(String username, int page, int size, String sortBy, String sortOrder);

}