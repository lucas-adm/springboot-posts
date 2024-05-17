package com.adm.lucas.posts.adapter.inbound.dtos.in.answer;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AnswerDTO(
        @NotBlank(message = "não pode ser vazio") @Size(min = 1, max = 255, message = "o tamanho não pode ultrapassar 255 caracteres.") String text
) {
}