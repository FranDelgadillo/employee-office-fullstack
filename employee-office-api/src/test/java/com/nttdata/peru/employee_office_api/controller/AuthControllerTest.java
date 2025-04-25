package com.nttdata.peru.employee_office_api.controller;

import com.nttdata.peru.employee_office_api.dto.UserDTO;
import com.nttdata.peru.employee_office_api.model.User;
import com.nttdata.peru.employee_office_api.security.TestSecurityConfig;
import com.nttdata.peru.employee_office_api.service.UserService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@WebFluxTest(AuthController.class)
@Import(TestSecurityConfig.class)
class AuthControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private UserService userService;

    private final UserDTO testUser = new UserDTO("user1", "password123");

    @Test
    void registerUser_Success() {
        User mockUser = new User();
        mockUser.setUsername("user1");
        mockUser.setPassword("encodedPassword");

        Mockito.when(userService.register(Mockito.any(UserDTO.class)))
                .thenReturn(Mono.just(mockUser));

        webTestClient.post()
                .uri("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testUser)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.username").isEqualTo("user1");
    }

    @Test
    void login_InvalidCredentials() {
        String errorMessage = "Credenciales inválidas";

        Mockito.when(userService.login(Mockito.any(UserDTO.class)))
                .thenReturn(Mono.error(new IllegalArgumentException(errorMessage)));

        webTestClient.post()
                .uri("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(testUser)
                .exchange()
                .expectStatus().isUnauthorized()
                .expectBody()
                .jsonPath("$.error").isEqualTo(errorMessage);
    }
}