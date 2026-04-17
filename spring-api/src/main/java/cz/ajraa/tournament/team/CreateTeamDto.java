package cz.ajraa.tournament.team;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateTeamDto {

    @NotBlank(message = "Jméno nesmí být prázdný.")
    private String name;
    @NotBlank(message = "Tag nesmí být prázdný.")
    @Size(min = 3, max = 3, message = "Tag musí mít 3 znaky")
    private String tag;
    private TeamType type = TeamType.TEAM;
}
