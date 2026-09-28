package com.nyumbaiq.backend.ai;

import com.nyumbaiq.backend.ai.dto.AiMessage;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Service
public class AiConversationService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration TTL = Duration.ofHours(4);

    public AiConversationService(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public List<AiMessage> getHistory(String conversationId) {
        List<Object> raw = redisTemplate.opsForList().range("ai:conversation:" + conversationId, 0, -1);
        if (raw == null || raw.isEmpty()) {
            return new ArrayList<>();
        }
        List<AiMessage> history = new ArrayList<>();
        for (Object item : raw) {
            if (item instanceof AiMessage message) {
                history.add(message);
            }
        }
        return history;
    }

    public void append(String conversationId, AiMessage message) {
        String key = "ai:conversation:" + conversationId;
        redisTemplate.opsForList().rightPush(key, message);
        redisTemplate.expire(key, TTL);
    }

    public void clear(String conversationId) {
        redisTemplate.delete("ai:conversation:" + conversationId);
    }
}
