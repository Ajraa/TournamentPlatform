package cz.ajraa.tournament.tournament.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/tournament")
@RequiredArgsConstructor
class TournamentController {

    private final TournamentService tournamentService;

    @PostMapping
    @PreAuthorize("hasRole('FOUNDER')")
    ResponseEntity<TournamentDto> createTournament(@RequestBody @Valid CreateTournamentDto dto, Authentication auth) {
        Long founderId = Long.parseLong(auth.getName());
        TournamentDto createdTournament = tournamentService.createTournament(dto, founderId);
        return ResponseEntity.ok().body(createdTournament);
    }
}
