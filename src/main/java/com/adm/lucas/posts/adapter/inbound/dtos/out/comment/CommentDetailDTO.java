package com.adm.lucas.posts.adapter.inbound.dtos.out.comment;

import java.time.LocalDateTime;
import java.util.UUID;

public record CommentDetailDTO(
        UUID uuid,
        String username,
        String photo,
        String text,
        String dateComment
) {
}
