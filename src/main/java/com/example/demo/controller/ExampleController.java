package com.example.demo.controller;

import com.example.demo.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/example")
@RequiredArgsConstructor
@Tag(name = "Аутентификация")
public class ExampleController {
    private final UserService service;

    /**
     * Тестовый метод
     * @return
     */
    @GetMapping
    @Operation(summary = "Доступен только авторизованным пользователям")
    public String example() {
        return "Hello, world!";
    }

    /**
     * Тестовый метод, доступный только админам
     * @return
     */
    @GetMapping("/admin")
    @Operation(summary = "Доступен только авторизованным пользователям с ролью ADMIN")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public String exampleAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("ROLES: " + authentication.getAuthorities());
        return "Hello, admin!";
    }

    /**
     * Тестовый метод, служащий для получения админки
     * @return
     */
    @GetMapping("/get-admin")
    @Operation(summary = "Получить роль ADMIN (для демонстрации)")
    public ResponseEntity<String> getAdmin() {
        return service.getAdmin();
    }
}
