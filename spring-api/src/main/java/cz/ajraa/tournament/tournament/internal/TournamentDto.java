package cz.ajraa.tournament.tournament.internal;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
class TournamentDto {

    private Long tournamentId;
    private String name;
    private LocalDateTime startTime;
    private BigDecimal prize;
    private BigDecimal price;
    private Integer minimalRating;
    private Integer maximalRating;
    private int playersPerTeam;
    private int minimalTeamAmount;
    private int maximalTeamAmount;
    private Long founderId;
    private Long winnerTeamId;
    private int registeredTeamsCount;
    private TournamentState state;
}
