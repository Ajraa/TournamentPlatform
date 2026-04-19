package cz.ajraa.tournament.user.internal;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@ValidUserRegistration
class UserRegistrationDto {

    @NotBlank(message = "Email nesmí být prázdný.")
    @Email(message = "Neplatný formát emailu")
    private String email;

    @NotBlank(message = "Heslo nesmí být prázdné.")
    @Size(min = 8, message = "Heslo musí mít alespoň 8 znaků.")
    private String password;

    @NotBlank(message = "Uživatelské jméno nesmí být prázdné.")
    private String nickname;

    private RoleType role = RoleType.PLAYER;

    // Required when role == FOUNDER (controlled by @ValidUserRegistration)
    private String firstName;
    private String lastName;
    private String street;
    private String houseNumber;
    private String city;
    private String postcode;
    private String country;
    private String bankAccount;
}
