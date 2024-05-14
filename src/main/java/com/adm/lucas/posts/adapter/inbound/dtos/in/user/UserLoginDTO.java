package com.adm.lucas.posts.adapter.inbound.dtos.in.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record UserLoginDTO(
        @NotBlank(message = "cannot be blank")
        @Size(min = 4, max = 33, message = "size must be between 4 and 25 .")
        String username,

        @NotBlank(message = "cannot be blank")
        @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", message = "must have lowercase letter, uppercase letter and number")
        @Size(min = 4, max = 33, message = "size must be between 4 and 8")
        String password) {
}