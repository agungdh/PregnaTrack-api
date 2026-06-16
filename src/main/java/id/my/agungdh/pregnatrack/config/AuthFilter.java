package id.my.agungdh.pregnatrack.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String TOKEN_PREFIX = "auth:token:";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Ambil header Authorization
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // 2. Validasi format header: Harus dimulai dengan "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        // 3. Ekstrak string Opaque Token-nya
        String token = authHeader.substring(7);
        String redisKey = TOKEN_PREFIX + token;

        // 4. Hit ke Redis/Valkey buat nyari UUID User
        Object cachedUuid = redisTemplate.opsForValue().get(redisKey);

        if (cachedUuid != null) {
            String userUuid = cachedUuid.toString();

            // 5. Daftarkan user ke Spring Security Context (Tanpa password & tanpa role ribet dulu)
            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userUuid, // Kita taruh UUID sebagai Principal
                    null,
                    Collections.emptyList() // Kosongkan authorities/roles sementara
            );

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // Lanjutkan ke filter berikutnya atau menuju Controller
        filterChain.doFilter(request, response);
    }
}