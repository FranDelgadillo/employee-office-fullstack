package com.nttdata.peru.employee_office_api.service;

import com.nttdata.peru.employee_office_api.dto.UserDTO;
import com.nttdata.peru.employee_office_api.model.User;
import com.nttdata.peru.employee_office_api.repository.UserRepository;
import com.nttdata.peru.employee_office_api.security.JWTUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@Tag(name = "User Service", description = "Servicios relacionados con los usuarios (registro y login)")
public class UserService {
    private final UserRepository userRepo;
    private final PasswordEncoder encoder = PasswordEncoderFactories.createDelegatingPasswordEncoder();
    private final JWTUtil jwtUtil;

    public UserService(UserRepository userRepo, JWTUtil jwtUtil) {
        this.userRepo = userRepo;
        this.jwtUtil = jwtUtil;
    }

    @Operation(summary = "Registrar un nuevo usuario",
            description = "Registra un nuevo usuario en el sistema.")
    @ApiResponse(responseCode = "200", description = "Usuario registrado con éxito")
    @ApiResponse(responseCode = "400", description = "El usuario ya existe")
    public Mono<User> register(UserDTO dto) {
        log.info("Iniciando registro de usuario: {}", dto.getUsername());
        return userRepo.findByUsername(dto.getUsername())
                .doOnNext(user -> log.warn("Intento de registrar usuario existente: {}", dto.getUsername()))
                .flatMap(u -> Mono.<User>error(new IllegalArgumentException("Usuario ya existe")))
                .switchIfEmpty(Mono.defer(() -> {
                    log.info("Creando nuevo usuario: {}", dto.getUsername());
                    User u = new User();
                    u.setUsername(dto.getUsername());
                    u.setPassword(encoder.encode(dto.getPassword()));
                    return userRepo.save(u)
                            .doOnSuccess(savedUser -> log.info("Usuario registrado exitosamente - ID: {}", savedUser.getId()))
                            .doOnError(e -> log.error("Error guardando usuario: {}", e.getMessage()));
                }))
                .doOnError(e -> log.error("Error en proceso de registro: {}", e.getMessage()));
    }

    @Operation(summary = "Iniciar sesión de un usuario",
            description = "Inicia sesión de un usuario y genera un token JWT.")
    @ApiResponse(responseCode = "200", description = "Login exitoso")
    @ApiResponse(responseCode = "400", description = "Credenciales inválidas")
    @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    public Mono<String> login(@Parameter(description = "Credenciales del usuario") UserDTO dto) {
        log.info("Intento de login para usuario: {}", dto.getUsername());
        return userRepo.findByUsername(dto.getUsername())
                .doOnNext(user -> log.debug("Usuario encontrado en base de datos"))
                .flatMap(u -> {
                    log.info("Verificando credenciales para usuario: {}", u.getUsername());
                    if (encoder.matches(dto.getPassword(), u.getPassword())) {
                        log.info("Credenciales válidas para usuario: {}", u.getUsername());
                        String token = jwtUtil.generateToken(u.getUsername());
                        log.debug("Token generado para usuario: {}", u.getUsername());
                        return Mono.just(token);
                    }
                    log.warn("Credenciales inválidas para usuario: {}", u.getUsername());
                    return Mono.error(new IllegalArgumentException("Credenciales inválidas"));
                })
                .doOnSuccess(token -> log.info("Login exitoso para usuario: {}", dto.getUsername()))
                .switchIfEmpty(Mono.defer(() -> {
                    log.error("Usuario no encontrado: {}", dto.getUsername());
                    return Mono.error(new IllegalArgumentException("Usuario no encontrado"));
                }))
                .doOnError(e -> log.error("Error en proceso de login: {}", e.getMessage()));
    }
}