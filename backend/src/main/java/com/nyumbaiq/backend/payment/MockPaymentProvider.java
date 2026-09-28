package com.nyumbaiq.backend.payment;

import com.nyumbaiq.backend.domain.enums.PaymentStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Component
public class MockPaymentProvider implements PaymentProvider {
    @Value("${app.payment.mock.mode:true}")
    private boolean mockMode;

    @Override
    public String initiatePayment(UUID tenantId, BigDecimal amount, String currency, String description, Map<String, String> metadata) {
        String reference = "MOCK-" + System.currentTimeMillis();
        return reference;
    }

    @Override
    public PaymentStatus checkPaymentStatus(String transactionReference) {
        return PaymentStatus.SUCCESS;
    }

    @Override
    public boolean verifyCallback(Map<String, String> callbackData) {
        return true;
    }

    @Override
    public PaymentResult processCallback(Map<String, String> callbackData) {
        String reference = callbackData.getOrDefault("transactionReference", "MOCK-" + System.currentTimeMillis());
        return new PaymentResult(reference, "SUCCESS", reference, Map.of());
    }

    @Override
    public RefundResult refundPayment(String transactionReference, BigDecimal amount, String reason) {
        return new RefundResult("REFUND-" + System.currentTimeMillis(), "SUCCESS", amount, reason);
    }
}
