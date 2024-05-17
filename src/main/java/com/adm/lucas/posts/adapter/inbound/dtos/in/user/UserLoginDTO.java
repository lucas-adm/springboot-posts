package com.adm.lucas.posts.adapter.inbound.dtos.in.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserLoginDTO(
        @NotBlank(message = "não pode ser vazio")
        @Size(min = 4, max = 33, message = "o tamanho deve estar entre 4 e 33 letras.")
        String username,

        @NotBlank(message = "não pode ser vazio")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "necessário letra maiúscula, minúscula e número.")
        @Size(min = 4, max = 33, message = "o tamanho deve estar entre 4 e 33 letras.")
        String password) {
}