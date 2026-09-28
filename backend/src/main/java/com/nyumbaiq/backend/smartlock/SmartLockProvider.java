package com.nyumbaiq.backend.smartlock;

import java.util.UUID;

public interface SmartLockProvider {
    void registerLock(String providerLockId, String lockCode, String name);
    LockStatus getLockStatus(String providerLockId);
    void lock(String providerLockId);
    void unlock(String providerLockId);
    String createAccessCredential(String providerLockId, String tenantId, String credentialType);
    void revokeAccessCredential(String providerLockId, String credentialCode);
    java.util.List<AccessEvent> getAccessEvents(String providerLockId);

    enum LockStatus {
        ONLINE, OFFLINE, ACTIVE, INACTIVE, MAINTENANCE
    }

    record AccessEvent(String eventId, String action, String result, long timestamp, String deviceInfo) {
    }
}
