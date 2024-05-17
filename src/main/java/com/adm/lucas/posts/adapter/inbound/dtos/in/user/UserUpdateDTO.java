package com.adm.lucas.posts.adapter.inbound.dtos.in.user;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserUpdateDTO(
        @NotBlank(message = "não pode ser vazio")
        @Email(message = "precisa ser um email válido.")
        String newEmail,

        @NotBlank(message = "não pode ser vazio")
        @Size(min = 4, max = 33, message = "o tamanho deve estar entre 4 e 33 letras.")
        String newUsername,

        @NotBlank(message = "não pode ser vazio")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "necessário letra maiúscula, minúscula e número.")
        @Size(min = 4, max = 33, message = "o tamanho deve estar entre 4 e 33 letras.")
        String newPassword,

        @NotNull(message = "não pode ser nulo")
        @Past(message = "precisa ser uma data válida.")
        LocalDate newBirthDate) {
}