package cz.ajraa.tournament.user.internal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
class ChangePasswordDto {
    @NotBlank(message = "Staré heslo nesmí být prázdné.")
    @Size(min = 8, message = "Staré heslo musí mít alespoň 8 znaků.")
    private String oldPassword;

    @NotBlank(message = "Nové heslo nesmí být prázdné.")
    @Size(min = 8, message = "Nové heslo musí mít alespoň 8 znaků.")
    private String newPassword;
}
