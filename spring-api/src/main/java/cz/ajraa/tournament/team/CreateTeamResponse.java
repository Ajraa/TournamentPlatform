package cz.ajraa.tournament.team;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class CreateTeamResponse {
    private Long teamId;
    private String message;
}
