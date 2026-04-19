package cz.ajraa.tournament.user.internal;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
class LoginDto {
    @NotBlank(message = "Nicmane nesmí být prázdný.")
    private String nickname;

    @NotBlank(message = "Heslo nesmí být prázdné.")
    private String password;
}
