package com.example.demo.auth;

import com.example.demo.schema.SignUpRequest;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.TestComponent;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@TestComponent
@RequiredArgsConstructor
public class AuthTests {
    private TestRestTemplate restTemplate;

    @Test
    public void registerUserTest() {
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setUsername("test");
        signUpRequest.setEmail("test@gmail.com");
        signUpRequest.setPassword("test123");
        ResponseEntity<String> response = restTemplate.postForEntity("/auth/register", signUpRequest, String.class);

        Assertions.assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
