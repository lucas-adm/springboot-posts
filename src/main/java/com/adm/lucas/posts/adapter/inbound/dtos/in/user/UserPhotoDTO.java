package com.adm.lucas.posts.adapter.inbound.dtos.in.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPhotoDTO(
        @NotBlank(message = "cannot be blank") @Size(min = 1, max = 255, message = "size must have between 1 and 255 characters.") String photo
) {
}