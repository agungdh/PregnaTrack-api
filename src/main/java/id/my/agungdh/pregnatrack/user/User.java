package id.my.agungdh.pregnatrack.user;

import id.my.agungdh.pregnatrack.config.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.SQLRestriction;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
// Intercept kueri delete bawaan menjadi Soft Delete dengan format Epoch Milli di Postgres
@SQLDelete(sql = "UPDATE users SET deleted_at = (EXTRACT(EPOCH FROM NOW()) * 1000)::bigint, deleted_by = NULL WHERE id = ?")
// Saringan global otomatis untuk mengabaikan data yang sudah di-soft delete pada setiap operasi SELECT
@SQLRestriction("deleted_at IS NULL")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, length = 100)
    private String name;
}