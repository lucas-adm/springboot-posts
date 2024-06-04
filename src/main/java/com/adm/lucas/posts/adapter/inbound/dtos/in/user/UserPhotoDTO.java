package com.adm.lucas.posts.adapter.inbound.dtos.in.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserPhotoDTO(
        @NotBlank(message = "não pode ser vazio") String photo
) {
}