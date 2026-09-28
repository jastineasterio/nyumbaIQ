package com.nyumbaiq.backend.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RevokeAccessCredentialRequest(@NotNull UUID credentialId) {
}
