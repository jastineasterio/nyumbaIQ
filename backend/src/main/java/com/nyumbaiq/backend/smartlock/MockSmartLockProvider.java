package com.nyumbaiq.backend.smartlock;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class MockSmartLockProvider implements SmartLockProvider {
    private final Map<String, MockLock> locks = new HashMap<>();
    private final Map<String, MockCredential> credentials = new HashMap<>();

    @Override
    public void registerLock(String providerLockId, String lockCode, String name) {
        locks.put(providerLockId, new MockLock(providerLockId, lockCode, name, LockStatus.ACTIVE));
    }

    @Override
    public LockStatus getLockStatus(String providerLockId) {
        MockLock lock = locks.get(providerLockId);
        return lock != null ? lock.status : LockStatus.OFFLINE;
    }

    @Override
    public void lock(String providerLockId) {
        MockLock lock = locks.get(providerLockId);
        if (lock != null) {
            lock.status = LockStatus.ACTIVE;
        }
    }

    @Override
    public void unlock(String providerLockId) {
        MockLock lock = locks.get(providerLockId);
        if (lock != null) {
            lock.status = LockStatus.ACTIVE;
        }
    }

    @Override
    public String createAccessCredential(String providerLockId, String tenantId, String credentialType) {
        String code = "CRED-" + System.currentTimeMillis();
        credentials.put(code, new MockCredential(code, providerLockId, tenantId, credentialType));
        return code;
    }

    @Override
    public void revokeAccessCredential(String providerLockId, String credentialCode) {
        MockCredential cred = credentials.get(credentialCode);
        if (cred != null && cred.providerLockId.equals(providerLockId)) {
            cred.revoked = true;
        }
    }

    @Override
    public List<AccessEvent> getAccessEvents(String providerLockId) {
        List<AccessEvent> events = new ArrayList<>();
        for (MockCredential cred : credentials.values()) {
            if (cred.providerLockId.equals(providerLockId)) {
                events.add(new AccessEvent(cred.code, "UNKNOWN", "SUCCESS", System.currentTimeMillis(), "mock"));
            }
        }
        return events;
    }

    private static class MockLock {
        String providerLockId;
        String lockCode;
        String name;
        LockStatus status;

        MockLock(String providerLockId, String lockCode, String name, LockStatus status) {
            this.providerLockId = providerLockId;
            this.lockCode = lockCode;
            this.name = name;
            this.status = status;
        }
    }

    private static class MockCredential {
        String code;
        String providerLockId;
        String tenantId;
        String credentialType;
        boolean revoked;

        MockCredential(String code, String providerLockId, String tenantId, String credentialType) {
            this.code = code;
            this.providerLockId = providerLockId;
            this.tenantId = tenantId;
            this.credentialType = credentialType;
            this.revoked = false;
        }
    }
}
