package cz.ajraa.tournament.tournament.internal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tournament")
@Getter
@Setter
@NoArgsConstructor
class Tournament {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long tournamentId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(precision = 12, scale = 2)
    private BigDecimal prize;

    @Column(precision = 12, scale = 2)
    private BigDecimal price;

    private Integer minimalRating;

    private Integer maximalRating;

    @Column(nullable = false)
    private int playersPerTeam;

    @Column(nullable = false)
    private int minimalTeamAmount;

    @Column(nullable = false)
    private int maximalTeamAmount;

    @Column(name = "founder_id", nullable = false)
    private Long founderId;

    @Column(name = "winner_team_id")
    private Long winnerTeamId;

    @ElementCollection
    @CollectionTable(name = "team_tournament", joinColumns = @JoinColumn(name = "tournament_id"))
    private Set<TeamRegistration> registrations = new HashSet<>();
}
