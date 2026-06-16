package id.my.agungdh.pregnatrack.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UserRequest(
        @NotBlank(message = "Email tidak boleh kosong")
        @Email(message = "Format email tidak valid")
        String email,

        @NotBlank(message = "Password tidak boleh kosong")
        @Size(min = 6, message = "Password minimal 6 karakter")
        String password,

        @NotBlank(message = "Nama tidak boleh kosong")
        String name
) {}