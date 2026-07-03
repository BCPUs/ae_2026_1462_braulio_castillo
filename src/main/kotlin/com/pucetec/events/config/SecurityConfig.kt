package com.pucetec.events.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.web.SecurityFilterChain

@Configuration
class SecurityConfig {
    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .csrf { it.disable() }
            .sessionManagement{it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)}
            //queremos definir como publico a todo lo que es /api/events y como privado, lo demas
            .authorizeHttpRequests { authRequest->
                authRequest
                    .requestMatchers("/api/events", "/api/events/**").permitAll()
                    .anyRequest().authenticated()
            }
            .oauth2ResourceServer { authRequest ->authRequest.jwt {} }
        return http.build()
    }
}