package com.adm.lucas.posts.core.ports.services;

import com.adm.lucas.posts.core.domain.Answer;

import java.util.List;
import java.util.UUID;

public interface AnswerServicePort {

    void answer(String username, UUID uuid, String text);

    void edit(String username, UUID uuid, String text);

    void remove(String username, UUID uuid);

    List<Answer> listCommentAnswers(UUID uuid,int page, int size, String sortBy, String sortOrder);

}