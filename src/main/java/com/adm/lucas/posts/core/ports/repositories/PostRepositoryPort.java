package com.adm.lucas.posts.core.ports.repositories;

import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.domain.User;

import java.util.List;
import java.util.UUID;

public interface PostRepositoryPort {

    void savePost(Post post);

    void removePost(Post post);

    Post findPostById(UUID uuid);

    User findUserByUsername(String username);

    List<Post> findAllPosts(int page, int size, String sortBy, String sortOrder);

    List<Post> findPostsByUsername(String username, int page, int size, String sortBy, String sortOrder);

}