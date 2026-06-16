package id.my.agungdh.pregnatrack.config;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.time.Instant;

@NoRepositoryBean
public interface BaseRepository<T extends BaseEntity, ID> extends JpaRepository<T, ID> {

    default void softDelete(T entity) {
        entity.setDeletedAt(Instant.now().toEpochMilli());
        entity.setDeletedBy(getCurrentUserId());
        save(entity);
    }

    private Long getCurrentUserId() {
        org.springframework.security.core.Authentication auth =
                org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return null;
        }
        Object principal = auth.getPrincipal();
        if (principal instanceof Long id) {
            return id;
        }
        if (principal instanceof Number n) {
            return n.longValue();
        }
        return null;
    }
}
