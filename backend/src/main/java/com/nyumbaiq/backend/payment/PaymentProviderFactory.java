package com.nyumbaiq.backend.payment;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class PaymentProviderFactory {
    private final PaymentProvider defaultProvider;

    public PaymentProviderFactory(@Qualifier("mockPaymentProvider") PaymentProvider defaultProvider) {
        this.defaultProvider = defaultProvider;
    }

    public PaymentProvider getProvider(String providerName) {
        if (providerName == null || providerName.isBlank() || "mock".equalsIgnoreCase(providerName)) {
            return defaultProvider;
        }
        throw new UnsupportedOperationException("Payment provider not supported: " + providerName);
    }
}
