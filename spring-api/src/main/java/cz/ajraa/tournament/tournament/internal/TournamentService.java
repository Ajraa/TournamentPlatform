package cz.ajraa.tournament.tournament.internal;

import cz.ajraa.tournament.tournament.TournamentPaymentRequiredEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class TournamentService {

    private final TournamentRepository tournamentRepository;
    private final ApplicationEventPublisher events;

    @Transactional
    TournamentDto createTournament(CreateTournamentDto dto, Long founderId) {

        Tournament tournament = new Tournament();

        tournament.setName(dto.getName());
        tournament.setStartTime(dto.getStartTime());
        tournament.setPrize(dto.getPrize());
        tournament.setPrice(dto.getPrice());
        tournament.setMinimalRating(dto.getMinimalRating());
        tournament.setMaximalRating(dto.getMaximalRating());
        tournament.setPlayersPerTeam(dto.getPlayersPerTeam());
        tournament.setMinimalTeamAmount(dto.getMinimalTeamAmount());
        tournament.setMaximalTeamAmount(dto.getMaximalTeamAmount());
        tournament.setFounderId(founderId);

        Tournament createdTournament = tournamentRepository.save(tournament);

        events.publishEvent(new TournamentPaymentRequiredEvent(
                createdTournament.getTournamentId(),
                founderId,
                createdTournament.getPrice()
        ));

        return toTournamentDto(createdTournament);
    }

    private TournamentDto toTournamentDto(Tournament tournament) {
        TournamentDto dto = new TournamentDto();
        dto.setTournamentId(tournament.getTournamentId());
        dto.setName(tournament.getName());
        dto.setStartTime(tournament.getStartTime());
        dto.setPrize(tournament.getPrize());
        dto.setPrice(tournament.getPrice());
        dto.setMinimalRating(tournament.getMinimalRating());
        dto.setMaximalRating(tournament.getMaximalRating());
        dto.setPlayersPerTeam(tournament.getPlayersPerTeam());
        dto.setMinimalTeamAmount(tournament.getMinimalTeamAmount());
        dto.setMaximalTeamAmount(tournament.getMaximalTeamAmount());
        dto.setFounderId(tournament.getFounderId());
        dto.setWinnerTeamId(tournament.getWinnerTeamId());
        dto.setRegisteredTeamsCount(tournament.getRegistrations().size());
        dto.setState(resolveState(tournament));
        return dto;
    }

    private TournamentState resolveState(Tournament tournament) {
        if (tournament.getWinnerTeamId() != null) {
            return TournamentState.FINISHED;
        }
        if (tournament.getStartTime().isBefore(LocalDateTime.now())) {
            return TournamentState.IN_PROGRESS;
        }
        return TournamentState.OPEN;
    }
}
