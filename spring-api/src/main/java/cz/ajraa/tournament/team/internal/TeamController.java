package cz.ajraa.tournament.team.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/v1/team")
@RequiredArgsConstructor
class TeamController {

    private final TeamService teamService;

    @PostMapping("/")
    ResponseEntity<CreateTeamResponse> createTeam(@Valid @RequestBody CreateTeamDto dto, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        CreateTeamResponse response = teamService.createTeam(dto, userId);
        return ResponseEntity.ok().body(response);
    }

}
