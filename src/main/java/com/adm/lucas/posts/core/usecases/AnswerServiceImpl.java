package com.adm.lucas.posts.core.usecases;

import com.adm.lucas.posts.core.domain.Answer;
import com.adm.lucas.posts.core.domain.Comment;
import com.adm.lucas.posts.core.domain.Status;
import com.adm.lucas.posts.core.domain.User;
import com.adm.lucas.posts.core.ports.repositories.AnswerRepositoryPort;
import com.adm.lucas.posts.core.ports.services.AnswerServicePort;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class AnswerServiceImpl implements AnswerServicePort {

    private final AnswerRepositoryPort repositoryPort;

    public AnswerServiceImpl(AnswerRepositoryPort repositoryPort) {
        this.repositoryPort = repositoryPort;
    }

    @Override
    public Answer answer(String username, UUID uuid, String text) {
        User user = repositoryPort.findUserByUsername(username);
        Comment comment = repositoryPort.findCommentById(uuid);
        if (comment.getPost().getStatus() == Status.CLOSED) {
            throw new RuntimeException("Este post está fechado.");
        }
        Answer answer = new Answer(user, comment, text);
        repositoryPort.saveAnswer(answer);
        return answer;
    }

    @Override
    public void edit(String username, UUID uuid, String text) {
        Answer answer = repositoryPort.findAnswerById(uuid);
        if (!Objects.equals(answer.getUser().getUsername(), username)) {
            throw new RuntimeException("Apenas o criador da resposta pode editar a resposta.");
        }
        answer.setText(text);
        repositoryPort.saveAnswer(answer);
    }

    @Override
    public void remove(String username, UUID uuid) {
        Answer answer = repositoryPort.findAnswerById(uuid);
        if (!Objects.equals(answer.getUser().getUsername(), username)) {
            throw new RuntimeException("Apenas o criador da resposta pode deletar a resposta.");
        }
        repositoryPort.removeAnswer(answer);
    }

    @Override
    public List<Answer> listCommentAnswers(UUID uuid, int page, int size, String sortBy, String sortOrder) {
        return repositoryPort.findAllCommentAnswers(uuid, page, size, sortBy, sortOrder);
    }

}