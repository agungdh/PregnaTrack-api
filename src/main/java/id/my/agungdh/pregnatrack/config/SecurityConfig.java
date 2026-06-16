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
                // 1. Matikan CSRF jika kamu mengembangkan REST API stateless (misal pake JWT)
                .csrf(AbstractHttpConfigurer::disable)

                // 2. Atur otorisasi request
                .authorizeHttpRequests(auth -> auth
                        // Daftarkan semua endpoint Swagger & OpenAPI agar diizinkan tanpa login
                        .requestMatchers(
                                "/v3/api-docs/**",
                                "/v3/api-docs.yaml",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**" // Sesuai path yang kamu set di application.yml kemarin
                        ).permitAll()

                        // Endpoint lain (misal API utama PregnaTrack) wajib login
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}