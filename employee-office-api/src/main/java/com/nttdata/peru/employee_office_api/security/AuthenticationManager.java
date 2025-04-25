package com.nttdata.peru.employee_office_api.security;

import io.jsonwebtoken.Claims;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Component
public class AuthenticationManager implements org.springframework.security.authentication.ReactiveAuthenticationManager {
    private final JWTUtil jwtUtil;

    public AuthenticationManager(JWTUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication auth) {
        String token = auth.getCredentials().toString();
        try {
            Claims claims = jwtUtil.validateToken(token);
            String username = claims.getSubject();
            var authority = Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));
            return Mono.just(new UsernamePasswordAuthenticationToken(username, null, authority));
        } catch (Exception e) {
            return Mono.error(new BadCredentialsException("Token inválido"));
        }
    }
}