package com.adm.lucas.posts.adapter.inbound.dtos.in;

import com.adm.lucas.posts.core.domain.User;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record UserRegisterDTO(
        @NotBlank(message = "cannot be blank")
        @Email(message = "must be a valid email.")
        String email,

        @NotBlank(message = "cannot be blank")
        @Size(min = 4, max = 33, message = "size must have between 4 and 33 characters.")
        String username,

        @NotBlank(message = "cannot be blank")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "must have lowercase letter, uppercase letter and number")
        @Size(min = 4, max = 33, message = "size must have between 4 and 33 characters.")
        String password,

        String photo,

        @NotNull(message = "cannot be null")
        @Past(message = "must be a valid date")
        LocalDate birthDate) {

    public User toUser() {
        return new User(email, username, password, photo, birthDate);
    }

}