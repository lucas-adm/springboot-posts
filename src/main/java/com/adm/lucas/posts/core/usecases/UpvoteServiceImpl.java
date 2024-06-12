package com.adm.lucas.posts.core.usecases;

import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.domain.Upvote;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.repositories.UpvoteRepositoryPort;
import com.adm.lucas.posts.core.ports.services.UpvoteServicePort;
import jakarta.persistence.EntityNotFoundException;

import java.util.UUID;

public class UpvoteServiceImpl implements UpvoteServicePort {

    private final UpvoteRepositoryPort repositoryPort;

    public UpvoteServiceImpl(UpvoteRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public void upvote(String username, UUID uuid) {
        User user = repositoryPort.findUserByUsername(username);
        Post post = repositoryPort.findPostById(uuid);
        if (user == null || post == null) {
            throw new EntityNotFoundException();
        }
        repositoryPort.saveUpvote(new Upvote(user, post));
    }

    @Override
    public Boolean getUpvote(String username, UUID uuid) {
        Upvote upvote = repositoryPort.findUpvoteByUserAndPost(username, uuid);
        return upvote != null;
    }

    @Override
    public void remove(String username, UUID uuid) {
        Upvote upvote = repositoryPort.findUpvoteByUserAndPost(username, uuid);
        repositoryPort.removeUpvote(upvote);
    }

}