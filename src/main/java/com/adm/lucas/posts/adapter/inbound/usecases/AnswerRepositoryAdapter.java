package com.adm.lucas.posts.adapter.inbound.usecases;

import com.adm.lucas.posts.adapter.inbound.entities.AnswerEntity;
import com.adm.lucas.posts.adapter.inbound.entities.CommentEntity;
import com.adm.lucas.posts.adapter.inbound.entities.UserEntity;
import com.adm.lucas.posts.adapter.inbound.repositories.AnswerRepository;
import com.adm.lucas.posts.adapter.inbound.repositories.CommentRepository;
import com.adm.lucas.posts.adapter.inbound.repositories.UserRepository;
import com.adm.lucas.posts.core.domain.Answer;
import com.adm.lucas.posts.core.domain.Comment;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.repositories.AnswerRepositoryPort;
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
public class AnswerRepositoryAdapter implements AnswerRepositoryPort {

    private final AnswerRepository repository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final ModelMapper modelMapper;

    @Override
    public User findUserByUsername(String username) {
        UserEntity user = userRepository.findByUsername(username).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(user, User.class);
    }

    @Override
    public Comment findCommentById(UUID uuid) {
        CommentEntity comment = commentRepository.findById(uuid).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(comment, Comment.class);
    }

    @Override
    public Answer findAnswerById(UUID uuid) {
        AnswerEntity answer = repository.findById(uuid).orElseThrow(EntityNotFoundException::new);
        return modelMapper.map(answer, Answer.class);
    }

    @Override
    public void saveAnswer(Answer answer) {
        repository.save(modelMapper.map(answer, AnswerEntity.class));
    }

    @Override
    public void removeAnswer(Answer answer) {
        CommentEntity comment = commentRepository.findById(answer.getComment().getId()).orElseThrow(EntityNotFoundException::new);
        comment.getAnswers().remove(modelMapper.map(answer, AnswerEntity.class));
    }

    @Override
    public List<Answer> findAllCommentAnswers(UUID uuid, int page, int size, String sortBy, String sortOrder) {
        Sort.Direction order = sortOrder.equals("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(order, sortBy);
        return repository.findAllAnswersByComment(uuid, PageRequest.of(page, size, sort)).stream().map(answerEntity -> modelMapper.map(answerEntity, Answer.class)).toList();
    }

}