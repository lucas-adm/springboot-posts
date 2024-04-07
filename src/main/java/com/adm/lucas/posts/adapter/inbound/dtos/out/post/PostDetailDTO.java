package com.adm.lucas.posts.adapter.inbound.dtos.out.post;

import com.adm.lucas.posts.core.domain.Post;
import com.adm.lucas.posts.core.domain.Status;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

public record PostDetailDTO(
        UUID id,
        String username,
        String photo,
        String text,
        String datePost,
        Status status
) {
    public PostDetailDTO(Post post) {
        this(post.getId(), post.getUsername(), post.getUser().getPhoto().orElse(null),
                post.getText(), post.getDatePost().format(DateTimeFormatter.ofPattern("d MMMM,yy HH:mm")), post.getStatus());
    }
}
