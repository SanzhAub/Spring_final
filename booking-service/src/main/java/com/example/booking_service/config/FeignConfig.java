package com.example.booking_service.config;

import feign.RequestInterceptor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Enumeration;

@Slf4j
@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor() {
        return requestTemplate -> {
            try {
                // Способ 1: Попробуем получить токен из SecurityContext
                Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
                
                if (authentication != null && authentication instanceof JwtAuthenticationToken) {
                    JwtAuthenticationToken jwtAuth = (JwtAuthenticationToken) authentication;
                    String tokenValue = jwtAuth.getToken().getTokenValue();
                    log.info("Feign: Adding JWT token from SecurityContext, user: {}", jwtAuth.getName());
                    requestTemplate.header("Authorization", "Bearer " + tokenValue);
                    return;
                }
                
                // Способ 2: Получить токен из текущего HTTP запроса
                ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
                if (attributes != null) {
                    HttpServletRequest request = attributes.getRequest();
                    String authHeader = request.getHeader("Authorization");
                    if (authHeader != null && authHeader.startsWith("Bearer ")) {
                        log.info("Feign: Adding Authorization header from request");
                        requestTemplate.header("Authorization", authHeader);
                        return;
                    }
                }
                
                log.warn("Feign: No JWT token found to pass to event-service");
                
            } catch (Exception e) {
                log.error("Feign: Error while adding authorization header", e);
            }
        };
    }
}