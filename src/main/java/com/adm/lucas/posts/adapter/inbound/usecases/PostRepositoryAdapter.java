package com.adm.lucas.posts.adapter.inbound.usecases;

import com.adm.lucas.posts.adapter.inbound.entities.PostEntity;
import com.adm.lucas.posts.adapter.inbound.entities.UserEntity;
import com.adm.lucas.posts.adapter.inbound.repositories.PostRepository;
import com.adm.lucas.posts.adapter.inbound.repositories.UserRepository;
import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.repositories.PostRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class PostRepositoryAdapter implements PostRepositoryPort {

    private final UserRepository userRepository;
    private final PostRepository repository;
    private final ModelMapper modelMapper;

    @Override
    public void savePost(Post post) {
        repository.save(modelMapper.map(post, PostEntity.class));
    }

    @Override
    public void removePost(Post post) {
        repository.delete(modelMapper.map(post, PostEntity.class));
    }

    @Override
    public Post findPostById(UUID uuid) {
        return modelMapper.map(repository.findById(uuid).orElseThrow(EntityNotFoundException::new), Post.class);
    }

    @Override
    public User findUserByUsername(String username) {
        UserEntity entity = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(entity, User.class);
    }

    @Override
    public List<Post> findPostsByUsername(String username, int page, int size, String sortBy, String sortOrder) {
        Sort.Direction order = sortOrder.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(order, sortBy);
        return repository.findByUserUsername(username, PageRequest.of(page, size, sort)).stream().map(postEntity -> modelMapper.map(postEntity, Post.class)).toList();
    }

    @Override
    public List<Post> findAllPosts(int page, int size, String sortBy, String sortOrder) {
        Sort.Direction order = sortOrder.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(order, sortBy);
        return repository.findAllPosts(PageRequest.of(page, size, sort)).stream().map(postEntity -> modelMapper.map(postEntity, Post.class)).toList();
    }

}