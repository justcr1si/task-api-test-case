package com.example.demo.service;

import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.exceptions.UserNotFoundException;
import com.example.demo.models.Role;
import com.example.demo.models.User;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

/**
 * Сервис, отвечающий за бизнес-логику по юзеру
 */
@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    /**
     * Метод, создающий юзера
     * @param user юзер
     */
    public void createUser(User user) {
        if (userRepository.existsByUsername(user.getUsername()) || userRepository.existsByEmail(user.getEmail())) {
            throw new UserAlreadyExistsException("User already exists");
        }

        userRepository.save(user);
    }

    /**
     * Метод, получающий юзера по почте
     * @param email почта
     * @return
     */
    public User getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
    }

    /**
     * Метод, получающий userDetails
     * @return
     */
    public UserDetailsService getUserDetailsService() {
        return new CustomUserDetailsService(userRepository);
    }

    /**
     * Метод получения текущего юзера по запросу
     * @return
     */
    public User getCurrentUser() {
        User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        var email = user.getEmail();
        return getUserByEmail(email);
    }


    // TEST

    /**
     * Метод, служащий получению админки, неактуальный, тестовый
     * @return
     * @deprecated исключительно для тестов, в реальной среде будет удален
     */
    @Deprecated
    public ResponseEntity<String> getAdmin() {
        var user = getCurrentUser();
        if (user != null) {
            user.setRole(Role.ADMIN);
            userRepository.save(user);
            return ResponseEntity.ok("User role set to ADMIN");
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("User not authenticated");
    }
}
