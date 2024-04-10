package com.adm.lucas.posts.adapter.inbound.dtos.in.comment;

import jakarta.validation.constraints.NotBlank;

public record CommentDTO(@NotBlank String text) {
}
