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
    public Post create(String username, String text) {
        User user = repositoryPort.findUserByUsername(username);
        Post post = new Post(user, text);
        repositoryPort.savePost(post);
        return post;
    }

    @Override
    public void edit(UUID uuid, String username, String text) {
        Post post = repositoryPort.findPostById(uuid);
        if (!Objects.equals(post.getUser().getUsername(), username)) {
            throw new RuntimeException("Apenas o criador do post pode editar o post.");
        }
        post.setText(text);
        repositoryPort.savePost(post);
    }

    @Override
    public void close(UUID uuid, String username) {
        Post post = repositoryPort.findPostById(uuid);
        if (!Objects.equals(post.getUser().getUsername(), username)) {
            throw new RuntimeException("Apenas o criador do post pode editar o post.");
        }
        post.setStatus(Status.INATIVO);
        repositoryPort.savePost(post);
    }

    @Override
    public void delete(UUID uuid, String username) {
        Post post = repositoryPort.findPostById(uuid);
        if (!Objects.equals(post.getUser().getUsername(), username)) {
            throw new RuntimeException("Apenas o criador do post pode editar o post.");
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