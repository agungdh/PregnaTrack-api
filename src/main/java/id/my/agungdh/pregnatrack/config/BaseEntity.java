package id.my.agungdh.pregnatrack.config;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.time.Instant;
import java.util.UUID;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseEntity {

    // UUID v4 untuk konsumsi Frontend (External ID)
    @Column(name = "uuid", nullable = false, updatable = false, length = 36)
    private String uuid;

    // Audit aktor menggunakan ID internal User (Long), nullable jika dari sistem
    @Column(name = "created_by", updatable = false)
    private Long createdBy;

    // Audit waktu menggunakan Epoch Milliseconds (bigint di PostgreSQL)
    @Column(name = "created_at", updatable = false)
    private Long createdAt;

    @Column(name = "updated_by")
    private Long updatedBy;

    @Column(name = "updated_at")
    private Long updatedAt;

    // Kolom untuk strategi Soft Delete
    @Column(name = "deleted_at")
    private Long deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    @PrePersist
    protected void onCreate() {
        // Dikunci dalam satu variabel lokal agar nilai waktu di kedua kolom identik secara atomik
        long now = Instant.now().toEpochMilli();
        this.createdAt = now;
        this.updatedAt = now;

        // Otomatis generate UUID v4 saat data pertama kali masuk
        if (this.uuid == null) {
            this.uuid = UUID.randomUUID().toString();
        }

        // Isi otomatis _by dari user yang sedang login (null jika anonymous)
        Long currentUserId = getCurrentUserId();
        this.createdBy = currentUserId;
        this.updatedBy = currentUserId;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now().toEpochMilli();
        this.updatedBy = getCurrentUserId();
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
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