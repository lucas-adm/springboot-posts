package com.adm.lucas.posts.adapter.inbound.usecases;

import com.adm.lucas.posts.adapter.inbound.entities.CommentEntity;
import com.adm.lucas.posts.adapter.inbound.entities.PostEntity;
import com.adm.lucas.posts.adapter.inbound.entities.UserEntity;
import com.adm.lucas.posts.adapter.inbound.repositories.CommentRepository;
import com.adm.lucas.posts.adapter.inbound.repositories.PostRepository;
import com.adm.lucas.posts.adapter.inbound.repositories.UserRepository;
import com.adm.lucas.posts.core.domain.Comment;
import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.repositories.CommentRepositoryPort;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class CommentRepositoryAdapter implements CommentRepositoryPort {

    private final CommentRepository repository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    public User findUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(user, User.class);
    }

    @Override
    public Post findPostById(UUID uuid) {
        PostEntity post = postRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(post, Post.class);
    }

    @Override
    public Comment findCommentById(UUID uuid) {
        CommentEntity comment = repository.findById(uuid).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(comment, Comment.class);
    }

    @Override
    public void saveComment(Comment comment) {
        repository.save(modelMapper.map(comment, CommentEntity.class));
    }

    @Override
    public void removeComment(Comment comment) {
        PostEntity post = postRepository.findById(comment.getPost().getId()).orElseThrow(EntityNotFoundException::new);
        post.getComments().remove(modelMapper.map(comment, CommentEntity.class));
    }

}