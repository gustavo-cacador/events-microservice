package com.gustavo.email_microservice.consumers;

import com.gustavo.email_microservice.domain.Email;
import com.gustavo.email_microservice.dtos.EmailRecordDTO;
import com.gustavo.email_microservice.services.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.BeanUtils;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class EmailConsumer {

    private final EmailService emailService;

    @RabbitListener(queues = "${broker.queue.email.name}")
    public void listenEmailQueue(@Payload EmailRecordDTO emailRecordDTO) {
        var email = new Email();
        BeanUtils.copyProperties(emailRecordDTO, email);
        emailService.sendEmail(email);
    }
}
