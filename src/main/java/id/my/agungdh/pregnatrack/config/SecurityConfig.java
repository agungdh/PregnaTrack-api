package id.my.agungdh.pregnatrack.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // 1. Matikan CSRF karena kita pake REST API berbasis stateless
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Atur otorisasi request
                .authorizeHttpRequests(auth -> auth
                        // Cuma izinkan Swagger, OpenAPI docs, dan Endpoint Login
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/api/auth/login" // <--- Murni ini doang buat jalur masuk
                        ).permitAll()

                        // Endpoint sisanya (termasuk CRUD user) mutlak wajib login
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}