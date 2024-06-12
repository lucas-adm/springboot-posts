package com.adm.lucas.posts.core.ports.services;

import com.adm.lucas.posts.core.domain.Upvote;

import java.util.UUID;

public interface UpvoteServicePort {

    void upvote(String username, UUID uuid);

    Boolean getUpvote(String username, UUID uuid);

    void remove(String username, UUID uuid);

}