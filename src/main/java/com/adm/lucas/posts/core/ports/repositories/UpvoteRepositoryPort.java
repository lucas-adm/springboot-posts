package com.adm.lucas.posts.core.ports.repositories;

import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.domain.Upvote;
import com.adm.lucas.posts.core.domain.User;

import java.util.UUID;

public interface UpvoteRepositoryPort {

    User findUserByUsername(String username);

    Post findPostById(UUID uuid);

    Upvote findUpvoteByUserAndPost(String username, UUID uuid);

    void saveUpvote(Upvote upvote);

    void removeUpvote(Upvote upvote);

}