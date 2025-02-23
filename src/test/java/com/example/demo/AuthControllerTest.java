package com.example.demo;

import com.example.demo.exceptions.UserAlreadyExistsException;
import com.example.demo.schema.JwtAuthenticationResponse;
import com.example.demo.schema.SignUpRequest;
import com.example.demo.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.CoreMatchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @Test
    void signUp_Success() throws Exception {
        SignUpRequest request = new SignUpRequest("user", "user@gmail.com", "userpassword");
        JwtAuthenticationResponse response = new JwtAuthenticationResponse("token");

        when(authService.signUp(any(SignUpRequest.class))).thenReturn(response);

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.token").value("token"));
    }

    @Test
    void signUp_UserAlreadyExists() throws Exception {
        SignUpRequest request = new SignUpRequest("user", "user@gmail.com", "userpassword");

        when(authService.signUp(any(SignUpRequest.class))).thenThrow(new UserAlreadyExistsException("User already exists"));

        mockMvc.perform(post("/auth/sign-up")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(new ObjectMapper().writeValueAsString(request)))
                        .andExpect(status().isConflict())
                        .andExpect(content().string(containsString("User already exists")));
    }
}
