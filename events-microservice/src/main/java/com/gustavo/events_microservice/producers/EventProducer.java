package com.gustavo.events_microservice.producers;

import com.gustavo.events_microservice.domain.Event;
import com.gustavo.events_microservice.domain.User;
import com.gustavo.events_microservice.dtos.EmailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EventProducer {

    private final RabbitTemplate rabbitTemplate;

    @Value(value = "${broker.queue.email.name}")
    private String routingKey;

    public void publishMessageEmail(User user, Event event) {
        var emailDTO = new EmailDTO();
        emailDTO.setUserId(user.getUserId());
        emailDTO.setEmailTo(user.getParticipantEmail());
        emailDTO.setSubject("Cadastro realizado com sucesso!");
        emailDTO.setText(user.getName() + ", seja bem vindo(a)! \nAgradecemos o seu cadastro no " + event.getTitle() + ", " + event.getDescription() +", aproveite o evento no dia: " + event.getDate() + "!");

        rabbitTemplate.convertAndSend("", routingKey, emailDTO);
    }
}
