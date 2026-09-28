package com.nyumbaiq.backend.audit;

import com.nyumbaiq.backend.domain.enums.AuditResult;
import com.nyumbaiq.backend.domain.entity.AuditLog;
import com.nyumbaiq.backend.service.AuditLogService;
import com.nyumbaiq.backend.service.RabbitMqProducer;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class AuditAspect {
    private final AuditLogService auditLogService;
    private final RabbitMqProducer rabbitMqProducer;

    public AuditAspect(AuditLogService auditLogService, RabbitMqProducer rabbitMqProducer) {
        this.auditLogService = auditLogService;
        this.rabbitMqProducer = rabbitMqProducer;
    }

    @Pointcut("within(@org.springframework.web.bind.annotation.RestController *)")
    public void restControllers() {}

    @AfterReturning(pointcut = "restControllers()", returning = "result")
    public void auditSuccess(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String entityType = extractEntityType(joinPoint);
        auditLogService.record(methodName, entityType, "N/A", AuditResult.SUCCESS);
    }

    @AfterThrowing(pointcut = "restControllers()", throwing = "ex")
    public void auditFailure(JoinPoint joinPoint, Exception ex) {
        String methodName = joinPoint.getSignature().getName();
        String entityType = extractEntityType(joinPoint);
        auditLogService.record(methodName, entityType, "N/A", AuditResult.FAILURE);
    }

    private String extractEntityType(JoinPoint joinPoint) {
        String className = joinPoint.getSignature().getDeclaringTypeName();
        return className.substring(className.lastIndexOf('.') + 1).replace("Controller", "");
    }
}
