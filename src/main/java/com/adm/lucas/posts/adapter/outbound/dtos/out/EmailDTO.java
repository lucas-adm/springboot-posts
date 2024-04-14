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
                user.getUsername() + ", seja bem-vindo(a)!\n\n" +
                        "Agradecemos seu cadastro, aproveite o Posts 🎉.\n\n" +
                        "Caso você não tenha feito o cadastramento no site, não se preocupe, é apenas uma aplicação teste onde os email não são validados ainda e este é o nosso sistema de mensageria, entre em contato conosco respondendo este email que iremos remover seu email de nosso banco de dados\n\n" +
                        "Atenciosamente, equipe Posts.\n\n\n" +
                        user.getUsername() + ", welcome!\n\n" +
                        "We appreciate your registration, enjoy Posts 🎉.\n\n" +
                        "If you have not registered on the site, do not worry, it is just a test application where emails are not validated yet and this is just our messaging system, contact us by replying to this email and we will remove your email from our database.\n\n" +
                        "Best regards, Posts team."
        );
    }
}