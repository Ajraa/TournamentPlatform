package cz.ajraa.tournament.team;

import cz.ajraa.tournament.common.exceptions.ResourceExistsException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
class TeamService {

    private final TeamRepository teamRepository;
    private final TeamTypeRepository teamTypeRepository;

    CreateTeamResponse createTeam(CreateTeamDto dto, Long captainId) {
        if (teamRepository.existsByName(dto.getName())) throw new ResourceExistsException("name", "Tým s tímto jménem již existuje");

        TeamTypeEntity teamType = teamTypeRepository.findByCode(dto.getType())
                .orElseThrow(() -> new IllegalStateException("Role %s neexistuje".formatted(dto.getType())));

        Team team = new Team();
        team.setName(dto.getName());
        team.setTag(dto.getTag());
        team.setType(teamType);
        team.setCaptainId(captainId);
        team.setRating(0); // Rating kapitána

        Team createdTeam = teamRepository.save(team);

        return new CreateTeamResponse(createdTeam.getTeamId(), "Tým byl vytvořen");
    }
}
