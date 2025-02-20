package com.example.demo.controller;

import com.example.demo.schema.JwtAuthenticationResponse;
import com.example.demo.schema.SignInRequest;
import com.example.demo.schema.SignUpRequest;
import com.example.demo.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication")
public class AuthController {
    private final AuthService authService;

    @Operation(summary = "Registration")
    @PostMapping("/sign-up")
    public ResponseEntity<JwtAuthenticationResponse> signUp(@RequestBody SignUpRequest signUpRequest) {
        JwtAuthenticationResponse token = authService.signUp(signUpRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(token);
    }

    @Operation(summary = "Authorization")
    @PostMapping("/sign-in")
    public ResponseEntity<JwtAuthenticationResponse> signIn(@RequestBody SignInRequest signInRequest) {
        JwtAuthenticationResponse token = authService.signIn(signInRequest);
        return ResponseEntity.ok(token);
    }
}
