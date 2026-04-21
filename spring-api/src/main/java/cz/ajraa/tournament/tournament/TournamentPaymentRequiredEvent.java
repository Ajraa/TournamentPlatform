package cz.ajraa.tournament.tournament;

import org.springframework.modulith.events.Externalized;

import java.math.BigDecimal;

@Externalized("payment.exchange")
public record TournamentPaymentRequiredEvent(
        Long tournamentId,
        Long organizerId,
        BigDecimal amount
) { }
