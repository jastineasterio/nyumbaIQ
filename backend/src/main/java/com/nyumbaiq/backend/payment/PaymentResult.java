package com.nyumbaiq.backend.payment;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record PaymentResult(String transactionReference, String status, String providerReference, Map<String, String> metadata) {
}
