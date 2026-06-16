package id.my.agungdh.pregnatrack.user;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    // Prefix biar key di Redis rapi, misal: auth:token:xxxx-xxxx
    private static final String TOKEN_PREFIX = "auth:token:";

    public String login(LoginRequest request) {
        // 1. Cari user berdasarkan email
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("Email atau password salah"));

        // 2. TODO: Validasi password pake BCrypt (sementara cocokin string polos dulu)
        if (!user.getPassword().equals(request.password())) {
            throw new RuntimeException("Email atau password salah");
        }

        // 3. Generate Opaque Token pake UUID acak yang panjang
        String opaqueToken = UUID.randomUUID().toString().replace("-", "");

        // 4. Simpan ke Redis. Key: auth:token:<string>, Value: user_uuid (atau user_id)
        // Kita set TTL nya 24 Jam
        String redisKey = TOKEN_PREFIX + opaqueToken;
        redisTemplate.opsForValue().set(redisKey, user.getUuid(), 24, TimeUnit.HOURS);

        // 5. Balikin token ke Frontend
        return opaqueToken;
    }
}