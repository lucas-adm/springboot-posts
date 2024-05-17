package com.adm.lucas.posts.adapter.inbound.dtos.out.answer;

import com.adm.lucas.posts.core.domain.Answer;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public record AnswerDetailDTO(
        UUID id,
        String username,
        String photo,
        String dateAnswer,
        String text
) {
    public AnswerDetailDTO(Answer answer) {
        this(
                answer.getId(),
                answer.getUser().getUsername(),
                answer.getUser().getPhoto().orElse(null),
                answer.getDateAnswer().format(DateTimeFormatter.ofPattern("d MMMM,yy HH:mm", new Locale("pt", "BR"))),
                answer.getText()
        );
    }
}