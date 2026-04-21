package cz.ajraa.tournament.tournament.internal;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@EqualsAndHashCode
class TeamRegistration {
    private Long teamId;
    private LocalDateTime joinedAt;
}
