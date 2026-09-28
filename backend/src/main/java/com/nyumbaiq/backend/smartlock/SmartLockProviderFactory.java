package com.nyumbaiq.backend.smartlock;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

@Component
public class SmartLockProviderFactory {
    private final SmartLockProvider defaultProvider;

    public SmartLockProviderFactory(@Qualifier("mockSmartLockProvider") SmartLockProvider defaultProvider) {
        this.defaultProvider = defaultProvider;
    }

    public SmartLockProvider getProvider(String providerName) {
        if (providerName == null || providerName.isBlank() || "mock".equalsIgnoreCase(providerName)) {
            return defaultProvider;
        }
        throw new UnsupportedOperationException("Smart lock provider not supported: " + providerName);
    }
}
