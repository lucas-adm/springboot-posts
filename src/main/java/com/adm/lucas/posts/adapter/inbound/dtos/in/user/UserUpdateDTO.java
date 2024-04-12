package com.adm.lucas.posts.adapter.inbound.dtos.in.user;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserUpdateDTO(
        @NotBlank(message = "cannot be blank")
        @Email(message = "must be a valid email.")
        String newEmail,

        @NotBlank(message = "cannot be blank")
        @Size(min = 4, max = 33, message = "size must have between 4 and 33 characters.")
        String newUsername,

        @NotBlank(message = "cannot be blank")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "must have lowercase letter, uppercase letter and number")
        @Size(min = 4, max = 33, message = "size must have between 4 and 33 characters.")
        String newPassword,

        @NotNull(message = "cannot be null")
        @Past(message = "must be a valid date")
        LocalDate newBirthDate) {
}