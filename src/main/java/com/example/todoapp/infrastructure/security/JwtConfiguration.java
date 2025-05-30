package com.example.todoapp.infrastructure.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "app.security.jwt")
@Getter
@Setter
public class JwtConfiguration {

    private String secretKey = "your-default-secret-key-that-is-at-least-256-bits-long-for-HS256-algorithm-security";
    private Duration expiration = Duration.ofHours(24);

    @Bean
    public JwtService jwtService() {
        return new JwtService(secretKey, expiration);
    }
}