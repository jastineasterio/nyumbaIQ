package com.nyumbaiq.backend.payment;

import java.math.BigDecimal;

public record RefundResult(String refundReference, String status, BigDecimal amount, String reason) {
}
