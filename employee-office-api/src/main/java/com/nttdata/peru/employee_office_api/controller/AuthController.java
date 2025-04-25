package com.nttdata.peru.employee_office_api.controller;

import com.nttdata.peru.employee_office_api.dto.UserDTO;
import com.nttdata.peru.employee_office_api.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import jakarta.validation.Valid;

import java.net.URI;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Autenticación", description = "Endpoints para registrar y autenticar usuarios")
class AuthController {
    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Registrar un nuevo usuario", responses = {
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Error en la solicitud")
    })
    @PostMapping("/register")
    public Mono<ResponseEntity<Map<String, String>>> register(@Valid @RequestBody UserDTO dto) {
        log.info("Iniciando registro de usuario: {}", dto.getUsername());
        return userService.register(dto)
                .doOnSuccess(user -> log.info("Registro exitoso para usuario: {}", user.getUsername()))
                .doOnError(e -> log.error("Error en registro de usuario: {}", e.getMessage()))
                .flatMap(user -> Mono.just(
                        ResponseEntity
                                .created(URI.create("/api/v1/auth/users/" + user.getUsername()))
                                .body(Map.of("username", user.getUsername()))
                ))
                .onErrorResume(e -> {
                    log.warn("Registro fallido para usuario: {} - Razón: {}", dto.getUsername(), e.getMessage());
                    return Mono.just(
                            ResponseEntity.badRequest().body(Map.of("error", e.getMessage()))
                    );
                });
    }

    @Operation(summary = "Iniciar sesión", responses = {
            @ApiResponse(responseCode = "200", description = "Inicio de sesión exitoso, retorna el token JWT"),
            @ApiResponse(responseCode = "401", description = "Credenciales incorrectas")
    })
    @PostMapping("/login")
    public Mono<ResponseEntity<Map<String, String>>> login(@Valid @RequestBody UserDTO dto) {
        log.info("Intento de login para usuario: {}", dto.getUsername());
        return userService.login(dto)
                .doOnSuccess(token -> log.info("Login exitoso para usuario: {}", dto.getUsername()))
                .doOnError(e -> log.error("Error en login: {}", e.getMessage()))
                .map(token -> ResponseEntity.ok(Map.of("token", token)))
                .onErrorResume(e -> {
                    log.warn("Login fallido para usuario: {} - Razón: {}", dto.getUsername(), e.getMessage());
                    return Mono.just(
                            ResponseEntity.status(401).body(Map.of("error", e.getMessage()))
                    );
                });
    }
}