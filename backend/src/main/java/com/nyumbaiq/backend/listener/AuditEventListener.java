package com.nyumbaiq.backend.listener;

import com.nyumbaiq.backend.event.AuditEventMessage;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
public class AuditEventListener {
    @RabbitListener(queues = "nyumbaiq.audit.queue")
    public void handleAuditEvent(AuditEventMessage event) {
        System.out.println("Received audit event: " + event.getAction() + " on " + event.getEntityType());
    }
}
