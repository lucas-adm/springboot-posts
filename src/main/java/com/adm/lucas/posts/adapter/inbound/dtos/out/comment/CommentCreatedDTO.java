package com.adm.lucas.posts.adapter.inbound.dtos.out.comment;

import com.adm.lucas.posts.core.domain.Comment;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.UUID;

public record CommentCreatedDTO(
        UUID id,
        String username,
        String photo,
        String text,
        String dateComment
) {
    public CommentCreatedDTO(Comment comment) {
        this(
                comment.getId(),
                comment.getUser().getUsername(),
                comment.getUser().getPhoto().orElse(null),
                comment.getText(),
                comment.getDateComment().format(DateTimeFormatter.ofPattern("d MMMM,yy HH:mm", new Locale("pt", "BR")))
        );
    }
}