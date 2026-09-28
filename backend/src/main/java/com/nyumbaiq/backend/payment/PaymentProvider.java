package com.nyumbaiq.backend.payment;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

import com.nyumbaiq.backend.domain.enums.PaymentStatus;

public interface PaymentProvider {
    String initiatePayment(UUID tenantId, BigDecimal amount, String currency, String description, Map<String, String> metadata);
    PaymentStatus checkPaymentStatus(String transactionReference);
    boolean verifyCallback(Map<String, String> callbackData);
    PaymentResult processCallback(Map<String, String> callbackData);
    RefundResult refundPayment(String transactionReference, BigDecimal amount, String reason);
}
