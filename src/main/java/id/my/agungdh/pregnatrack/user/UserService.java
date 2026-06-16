package id.my.agungdh.pregnatrack.user;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    // Inject lewat constructor
    public UserService(UserRepository userRepository, UserMapper userMapper, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserResponse createUser(UserRequest request) {
        // 1. Validasi duplikasi email
        if (userRepository.existsByEmail(request.email())) {
            throw new RuntimeException("Email sudah terdaftar!");
        }

        // 2. Konversi DTO ke Entity pake MapStruct
        User user = userMapper.toEntity(request);

        // 3. Hash password sebelum simpan
        user.setPassword(passwordEncoder.encode(request.password()));

        // 4. Simpan ke Postgres
        User savedUser = userRepository.save(user);

        // 5. Konversi balik ke DTO Response buat dikirim ke Controller
        return userMapper.toResponse(savedUser);
    }
}