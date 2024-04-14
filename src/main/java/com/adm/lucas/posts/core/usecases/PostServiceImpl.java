package com.adm.lucas.posts.core.usecases;

import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.domain.Status;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.repositories.PostRepositoryPort;
import com.adm.lucas.posts.core.ports.services.PostServicePort;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class PostServiceImpl implements PostServicePort {

    private final PostRepositoryPort repositoryPort;

    public PostServiceImpl(PostRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public void create(String username, String text) {
        User user = repositoryPort.findUserByUsername(username);
        repositoryPort.savePost(new Post(user, text));
    }

    @Override
    public void edit(UUID uuid, String username, String text) {
        Post post = repositoryPort.findPostById(uuid);
        if (!Objects.equals(post.getUsername(), username)) {
            throw new RuntimeException("Only the post user can edit this post.");
        }
        post.setText(text);
        repositoryPort.savePost(post);
    }

    @Override
    public void close(UUID uuid, String username) {
        Post post = repositoryPort.findPostById(uuid);
        if (!Objects.equals(post.getUsername(), username)) {
            throw new RuntimeException("Only the post user can edit this post.");
        }
        post.setStatus(Status.CLOSED);
        repositoryPort.savePost(post);
    }

    @Override
    public void delete(UUID uuid, String username) {
        Post post = repositoryPort.findPostById(uuid);
        if (!Objects.equals(post.getUsername(), username)) {
            throw new RuntimeException("Only the post user can edit this post.");
        }
        repositoryPort.removePost(post);
    }

    @Override
    public Post select(UUID uuid) {
        return repositoryPort.findPostById(uuid);
    }

    @Override
    public List<Post> listPosts(int page, int size, String sortBy, String sortOrder) {
        return repositoryPort.findAllPosts(page, size, sortBy, sortOrder);
    }

    @Override
    public List<Post> listUserPosts(String username, int page, int size, String sortBy, String sortOrder) {
        return repositoryPort.findPostsByUsername(username, page, size, sortBy, sortOrder);
    }

}