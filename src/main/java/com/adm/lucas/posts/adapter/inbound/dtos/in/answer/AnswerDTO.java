package com.adm.lucas.posts.adapter.inbound.dtos.in.answer;

import jakarta.validation.constraints.NotBlank;

public record AnswerDTO(@NotBlank String text) {
}