package com.adm.lucas.posts.adapter.outbound.dtos.out;

import com.adm.lucas.posts.core.domain.User;

import java.util.UUID;

public record EmailDTO(
        UUID id,
        String emailTo,
        String subject,
        String text
) {
    public EmailDTO(User user) {
        this(
                user.getId(),
                user.getEmail(),
                "Cadastro realizado com sucesso!",
                user.getUsername() + ", seja bem-vindo(a)! \nAgradecemos seu cadastro, aproveite o Posts 😈."
        );
    }
}