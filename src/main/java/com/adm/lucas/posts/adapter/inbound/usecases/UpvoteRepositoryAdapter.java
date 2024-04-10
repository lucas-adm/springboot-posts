package com.adm.lucas.posts.adapter.inbound.usecases;

import com.adm.lucas.posts.adapter.inbound.entities.PostEntity;
import com.adm.lucas.posts.adapter.inbound.entities.UpvoteEntity;
import com.adm.lucas.posts.adapter.inbound.entities.UserEntity;
import com.adm.lucas.posts.adapter.inbound.repositories.PostRepository;
import com.adm.lucas.posts.adapter.inbound.repositories.UpvoteRepository;
import com.adm.lucas.posts.adapter.inbound.repositories.UserRepository;
import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.domain.Upvote;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.repositories.UpvoteRepositoryPort;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class UpvoteRepositoryAdapter implements UpvoteRepositoryPort {

    private final UpvoteRepository repository;
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final ModelMapper modelMapper;

    @Override
    public User findUserByUsername(String username) {
        return modelMapper.map(userRepository.findByUsername(username), User.class);
    }

    @Override
    public Post findPostById(UUID uuid) {
        return modelMapper.map(postRepository.findById(uuid), Post.class);
    }

    @Override
    public Upvote findUpvoteByUserAndPost(String username, UUID uuid) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        PostEntity post = postRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
        UpvoteEntity upvote = repository.findByUserAndPost(user, post).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(upvote, Upvote.class);
    }

    @Override
    public void saveUpvote(Upvote upvote) {
        UserEntity user = userRepository.findById(upvote.getUser().getId()).orElseThrow(EntityNotFoundException::new);
        PostEntity post = postRepository.findById(upvote.getPost().getId()).orElseThrow(EntityNotFoundException::new);
        if (repository.findByUserAndPost(user, post).isPresent()) {
            throw new EntityExistsException();
        }
        repository.save(modelMapper.map(upvote, UpvoteEntity.class));
    }

    @Override
    public void removeUpvote(Upvote upvote) {
        PostEntity post = postRepository.findById(upvote.getPost().getId()).orElseThrow(EntityNotFoundException::new);
        post.getUpvotes().remove(modelMapper.map(upvote, UpvoteEntity.class));
    }

}