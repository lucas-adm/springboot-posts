package com.adm.lucas.posts.adapter.inbound.dtos.in;

import java.time.LocalDate;
import java.util.Optional;

public record UserRegisterDTO(
        String email,
        String username,
        String password,
        String photo,
        LocalDate birthDate) {
}
