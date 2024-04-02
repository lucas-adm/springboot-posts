package com.adm.lucas.posts.adapter.inbound.dtos.in;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public record UserUpdateDTO(
        String email,
        String username,
        String password,
        Optional<String> photo,
        LocalDate birthDate) {
}