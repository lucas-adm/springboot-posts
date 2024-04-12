package com.adm.lucas.posts.core.ports.repositories;

import com.adm.lucas.posts.core.domain.Answer;
import com.adm.lucas.posts.core.domain.Comment;
import com.adm.lucas.posts.core.domain.User;

import java.util.List;
import java.util.UUID;

public interface AnswerRepositoryPort {

    User findUserByUsername(String username);

    Comment findCommentById(UUID uuid);

    Answer findAnswerById(UUID uuid);

    void saveAnswer(Answer answer);

    void removeAnswer(Answer answer);

    List<Answer> findAllCommentAnswers(UUID uuid, int page, int size, String sortBy, String sortOrder);

}