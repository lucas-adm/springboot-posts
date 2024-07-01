package com.adm.lucas.posts.adapter.outbound.producers;

import com.adm.lucas.posts.adapter.outbound.dtos.out.EmailDTO;
import com.adm.lucas.posts.adapter.outbound.dtos.out.NewPasswordEmailDTO;
import com.adm.lucas.posts.core.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value(value = "${broker.queue.email.name}")
    private String routingKey;

    @Value(value = "${broker.queue.recover.name}")
    private String recoverRoutingKey;

    public void publishMessageEmail(User user) {
        var email = new EmailDTO(user);
        rabbitTemplate.convertAndSend("", routingKey, email);
    }

    public void publishRecoverEmail(String email, String token) {
        var message = new NewPasswordEmailDTO(email, token);
        rabbitTemplate.convertAndSend("", recoverRoutingKey, message);
    }

}