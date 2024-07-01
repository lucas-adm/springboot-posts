package com.adm.lucas.posts.adapter.outbound.dtos.out;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record NewPasswordEmailDTO(
        String emailTo,
        String subject,
        String text
) {
    public static String text(String email, String token) {
        try {
            ClassPathResource resource = new ClassPathResource("/templates/mail-template/recover.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template = template.replace("{token}", token);
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public NewPasswordEmailDTO(String email, String token) {
        this(
                email,
                "Nova senha",
                text(email, token)
        );
    }
}