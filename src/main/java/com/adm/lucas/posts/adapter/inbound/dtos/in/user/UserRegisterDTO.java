package com.adm.lucas.posts.adapter.inbound.dtos.in.user;

import com.adm.lucas.posts.core.domain.User;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRegisterDTO(
        @NotBlank(message = "não pode ser vazio")
        @Email(message = "precisa ser um email válido.")
        String email,

        @NotBlank(message = "não pode ser vazio")
        @Size(min = 4, max = 33, message = "o tamanho deve estar entre 4 e 33 letras.")
        String username,

        @NotBlank(message = "não pode ser vazio")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "necessário letra maiúscula, minúscula e número.")
        @Size(min = 4, max = 33, message = "o tamanho deve estar entre 4 e 33 letras.")
        String password,

        String photo,

        @NotNull(message = "não pode ser nulo")
        @Past(message = "precisa ser uma data válida.")
        LocalDate birthDate
) {
    public User toUser() {
        return new User(email, username, password, photo, birthDate);
    }
}