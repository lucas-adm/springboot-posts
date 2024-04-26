package com.adm.lucas.posts.adapter.outbound.dtos.out;

import com.adm.lucas.posts.core.domain.User;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record EmailDTO(
        UUID id,
        String emailTo,
        String subject,
        String text
) {
    public static String text(UUID id, String username) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/mail-template/mail.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template = template.replace("{username}", username).replace("{userId}", id.toString());
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public EmailDTO(User user) {
        this(
                user.getId(),
                user.getEmail(),
                "Cadastro realizado com sucesso!",
                text(user.getId(), user.getUsername())
        );
    }
}