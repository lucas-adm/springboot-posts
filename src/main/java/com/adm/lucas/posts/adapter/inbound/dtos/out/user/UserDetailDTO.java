package com.adm.lucas.posts.adapter.inbound.dtos.out.user;

import com.adm.lucas.posts.core.domain.Role;
import com.adm.lucas.posts.core.domain.User;

import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public record UserDetailDTO(UUID id, String email, String username, Optional<String> photo, String birthDate, Role role) {
    public UserDetailDTO(User user) {
        this(
                user.getId(),
                user.getEmail(),
                user.getUsername(),
                user.getPhoto(),
                user.getBirthDate().format(DateTimeFormatter.ofPattern("d MMMM, yyyy", new Locale("pt","BR"))),
                user.getRole()
        );
    }
}
