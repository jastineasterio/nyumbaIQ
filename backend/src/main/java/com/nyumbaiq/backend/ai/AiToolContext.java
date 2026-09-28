package com.nyumbaiq.backend.ai;

import com.nyumbaiq.backend.domain.entity.User;
import com.nyumbaiq.backend.security.CurrentUser;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AiToolContext {
    private final CurrentUser currentUser;
    private final User user;

    public AiToolContext(CurrentUser currentUser) {
        this.currentUser = currentUser;
        this.user = currentUser.getUser();
    }

    public UUID getUserId() {
        return currentUser.getUserId();
    }

    public User getUser() {
        return user;
    }

    public com.nyumbaiq.backend.domain.enums.Role getRole() {
        return user.getRole();
    }

    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", user.getId().toString());
        map.put("role", user.getRole().name());
        map.put("email", user.getEmail());
        map.put("firstName", user.getFirstName());
        map.put("lastName", user.getLastName());
        return map;
    }
}
