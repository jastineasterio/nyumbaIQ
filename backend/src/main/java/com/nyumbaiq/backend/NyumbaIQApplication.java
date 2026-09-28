package com.nyumbaiq.backend;

import com.nyumbaiq.backend.ai.GeminiProperties;
import com.nyumbaiq.backend.security.JwtProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@EnableConfigurationProperties({GeminiProperties.class, JwtProperties.class})
public class NyumbaIQApplication {

    public static void main(String[] args) {
        SpringApplication.run(NyumbaIQApplication.class, args);
    }
}
