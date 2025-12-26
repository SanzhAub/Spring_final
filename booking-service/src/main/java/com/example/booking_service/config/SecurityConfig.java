package com.example.booking_service.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.DelegatingOAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2TokenValidator;
import org.springframework.security.oauth2.core.OAuth2TokenValidatorResult;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                "/public/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()
                        // В booking-service ВСЕ /api/ endpoints требуют аутентификации
                        // Для создания бронирования нужна роль USER, для отмены - тоже USER
                        .requestMatchers("/api/**").authenticated()  // или .hasRole("USER")
                        .anyRequest().permitAll()
                )
                .oauth2ResourceServer(oauth2 ->
                        oauth2.jwt(jwt -> {
                            jwt.decoder(jwtDecoder());
                            jwt.jwtAuthenticationConverter(jwtAuthConverter());
                        })
                );

        return http.build();
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder
                .withJwkSetUri("http://keycloak:8080/realms/spring-final/protocol/openid-connect/certs")
                .build();

        OAuth2TokenValidator<Jwt> localhostValidator = 
            new JwtIssuerValidator("http://localhost:8080/realms/spring-final");
        OAuth2TokenValidator<Jwt> keycloakValidator = 
            new JwtIssuerValidator("http://keycloak:8080/realms/spring-final");

        OAuth2TokenValidator<Jwt> combinedValidator = token -> {
                OAuth2TokenValidatorResult localhostResult = localhostValidator.validate(token);
                if (!localhostResult.hasErrors()) {
                        return OAuth2TokenValidatorResult.success();
                }

                OAuth2TokenValidatorResult keycloakResult = keycloakValidator.validate(token);
                if (!keycloakResult.hasErrors()) {
                        return OAuth2TokenValidatorResult.success();
                }

                // Создаём объект ошибки
                OAuth2Error error = new OAuth2Error(
                        "invalid_token",
                        "Token issuer must be either http://localhost:8080/realms/spring-final or http://keycloak:8080/realms/spring-final",
                        null
                );

                return OAuth2TokenValidatorResult.failure(error);
        };

        OAuth2TokenValidator<Jwt> withTimestamp = new DelegatingOAuth2TokenValidator<>(
                new JwtTimestampValidator(),
                combinedValidator
        );

        jwtDecoder.setJwtValidator(withTimestamp);
        return jwtDecoder;
    }

    @Bean
    JwtAuthenticationConverter jwtAuthConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            if (realmAccess == null || realmAccess.get("roles") == null) {
                return List.of();
            }
            
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) realmAccess.get("roles");
            
            return roles.stream()
                    .map(role -> new SimpleGrantedAuthority("ROLE_" + role))
                    .collect(Collectors.toList());
        });
        return converter;
    }
}