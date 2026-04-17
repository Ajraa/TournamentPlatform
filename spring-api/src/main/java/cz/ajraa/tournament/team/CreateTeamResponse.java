package cz.ajraa.tournament.team;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class CreateTeamResponse {
    private Long teamId;
    private String message;
}
