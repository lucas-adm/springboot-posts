package com.adm.lucas.posts.adapter.inbound.dtos.out.post;

import com.adm.lucas.posts.adapter.inbound.dtos.out.comment.CommentDetailDTO;
import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.domain.Status;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public record PostDetailDTO(
        UUID id,
        String username,
        String photo,
        String text,
        String datePost,
        Status status,
        int upvotes,
        List<CommentDetailDTO> comments
) {
    public PostDetailDTO(Post post) {
        this(
                post.getId(),
                post.getUser().getUsername(),
                post.getUser().getPhoto().orElse(null),
                post.getText(),
                post.getDatePost().format(DateTimeFormatter.ofPattern("d MMMM,yy HH:mm", new Locale("pt", "BR"))),
                post.getStatus(),
                post.getUpvotes().size(),
                post.getComments().stream().map(comment -> new CommentDetailDTO(
                        comment.getId(),
                        comment.getUser().getUsername(),
                        comment.getUser().getPhoto().orElse(null),
                        comment.getText(),
                        comment.getDateComment().format(DateTimeFormatter.ofPattern("d MMMM,yy HH:mm", new Locale("pt", "BR"))),
                        comment.getAnswerCount()
                )).toList()
        );
    }
}