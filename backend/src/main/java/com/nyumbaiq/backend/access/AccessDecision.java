package com.nyumbaiq.backend.access;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class AccessDecision {
    public enum State {
        ALLOWED,
        RESTRICTED,
        SUSPENDED,
        EMERGENCY_ACCESS,
        MAINTENANCE_ACCESS
    }

    private State state;
    private String reason;
    private boolean requiresOtp;
    private boolean emergencyAccess;

    public static AccessDecision allowed(String reason) {
        return new AccessDecision(State.ALLOWED, reason, false, false);
    }

    public static AccessDecision restricted(String reason) {
        return new AccessDecision(State.RESTRICTED, reason, false, false);
    }

    public static AccessDecision suspended(String reason) {
        return new AccessDecision(State.SUSPENDED, reason, false, false);
    }

    public static AccessDecision emergencyAccess(String reason) {
        return new AccessDecision(State.EMERGENCY_ACCESS, reason, true, true);
    }

    public static AccessDecision maintenanceAccess(String reason) {
        return new AccessDecision(State.MAINTENANCE_ACCESS, reason, false, false);
    }
}
