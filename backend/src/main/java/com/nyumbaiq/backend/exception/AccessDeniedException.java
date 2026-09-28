package com.nyumbaiq.backend.exception;

import org.springframework.http.HttpStatus;

public class AccessDeniedException extends ApiException {
    public AccessDeniedException(String message) {
        super(HttpStatus.FORBIDDEN, message);
    }
}
