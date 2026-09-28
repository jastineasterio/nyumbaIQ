package com.nyumbaiq.backend.service;

import com.nyumbaiq.backend.event.AuditEventMessage;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RabbitMqProducer {
    private final RabbitTemplate rabbitTemplate;

    @Value("${spring.rabbitmq.exchange:nyumbaiq.exchange}")
    private String exchange;

    public RabbitMqProducer(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void sendAuditEvent(AuditEventMessage event) {
        rabbitTemplate.convertAndSend(exchange, "audit.event", event);
    }
}
