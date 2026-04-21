package cz.ajraa.tournament.tournament.internal;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "match")
@Getter
@Setter
@NoArgsConstructor
class Match {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long matchId;

    @Enumerated(EnumType.STRING)
    @Column(name = "match_state", nullable = false, length = 50)
    private MatchState matchState;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "tournament_id", nullable = false)
    private Tournament tournament;

    @Column(name = "winner_team_id")
    private Long winnerTeamId;

    @ElementCollection
    @CollectionTable(name = "team_match", joinColumns = @JoinColumn(name = "match_id"))
    @Column(name = "team_id")
    private Set<Long> teamIds = new HashSet<>();
}
