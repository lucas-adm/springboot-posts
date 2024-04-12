package com.adm.lucas.posts.adapter.inbound.dtos.in.user;

import jakarta.validation.constraints.NotBlank;

public record UserPhotoDTO(@NotBlank String photo) {
}