package id.my.agungdh.pregnatrack.user;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // Untuk ngecek duplikasi email pas daftar (create)
    boolean existsByEmail(String email);

    // Untuk nyari user berdasarkan email pas login (auth)
    Optional<User> findByEmail(String email);

    // Untuk nyari user berdasarkan UUID dari request Frontend
    Optional<User> findByUuid(String uuid);
}