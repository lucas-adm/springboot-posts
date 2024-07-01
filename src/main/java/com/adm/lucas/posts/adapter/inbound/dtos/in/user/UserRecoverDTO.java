package com.adm.lucas.posts.adapter.inbound.dtos.in.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record UserRecoverDTO(
        @NotBlank(message = "não pode ser vazio")
        @Email(message = "precisa ser um email válido.")
        String email) {
}