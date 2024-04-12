package com.adm.lucas.posts.adapter.inbound.dtos.out.comment;

import java.util.UUID;

public record CommentDetailDTO(
        UUID id,
        String username,
        String photo,
        String text,
        String dateComment,
        int answers
) {
}
