package cz.ajraa.tournament.team.internal;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
class CreateTeamResponse {
    private Long teamId;
    private String message;
}
