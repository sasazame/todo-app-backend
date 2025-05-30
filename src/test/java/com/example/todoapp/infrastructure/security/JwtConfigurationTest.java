package com.example.todoapp.infrastructure.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "app.security.jwt.secret-key=test-secret-key-that-is-at-least-256-bits-long-for-HS256-algorithm",
    "app.security.jwt.expiration=3600000"
})
class JwtConfigurationTest {

    @Autowired
    private JwtConfiguration jwtConfiguration;

    @Test
    void shouldLoadJwtConfigurationFromProperties() {
        assertNotNull(jwtConfiguration);
        assertEquals("test-secret-key-that-is-at-least-256-bits-long-for-HS256-algorithm", 
                     jwtConfiguration.getSecretKey());
        assertEquals(Duration.ofMillis(3600000), jwtConfiguration.getExpiration());
    }

    @Test
    void shouldCreateJwtServiceWithConfiguredValues() {
        JwtService jwtService = jwtConfiguration.jwtService();
        
        assertNotNull(jwtService);
    }
}