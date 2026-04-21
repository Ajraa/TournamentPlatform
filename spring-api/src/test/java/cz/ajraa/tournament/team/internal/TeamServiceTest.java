package cz.ajraa.tournament.team.internal;

import cz.ajraa.tournament.common.exceptions.ResourceExistsException;
import cz.ajraa.tournament.user.UserFacade;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TeamServiceTest {

    @Mock
    private TeamRepository teamRepository;

    @Mock
    private TeamTypeRepository teamTypeRepository;

    @Mock
    private UserFacade userFacade;

    @InjectMocks
    private TeamService teamService;

    // ─── helpers ───────────────────────────────────────────────────────────────

    private TeamTypeEntity teamTypeEntityOf(TeamType type) {
        TeamTypeEntity entity = new TeamTypeEntity();
        entity.setCode(type);
        return entity;
    }

    private CreateTeamDto createDto() {
        CreateTeamDto dto = new CreateTeamDto();
        dto.setName("Testovaci tym");
        dto.setTag("TST");
        dto.setType(TeamType.TEAM);
        return dto;
    }

    // ─── createTeam — chybové stavy ────────────────────────────────────────────

    @Test
    void createTeam_existujiciNazev_hodiResourceExistsException() {
        when(teamRepository.existsByName("Testovaci tym")).thenReturn(true);

        assertThatThrownBy(() -> teamService.createTeam(createDto(), 1L))
            .isInstanceOf(ResourceExistsException.class)
            .satisfies(ex -> assertThat(((ResourceExistsException) ex).getField()).isEqualTo("name"));

        verify(teamRepository, never()).save(any());
    }

    @Test
    void createTeam_neexistujiciTypTymu_hodiIllegalStateException() {
        when(teamRepository.existsByName(any())).thenReturn(false);
        when(teamTypeRepository.findByCode(TeamType.TEAM)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> teamService.createTeam(createDto(), 1L))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("TEAM");
    }

    // ─── createTeam — úspěch ───────────────────────────────────────────────────

    @Test
    void createTeam_validniData_uloziSpravnaData() {
        CreateTeamDto dto = createDto();
        when(teamRepository.existsByName(dto.getName())).thenReturn(false);
        when(teamTypeRepository.findByCode(TeamType.TEAM)).thenReturn(Optional.of(teamTypeEntityOf(TeamType.TEAM)));
        when(userFacade.getCaptainRating(1L)).thenReturn(750);

        Team savedTeam = new Team();
        savedTeam.setName(dto.getName());
        savedTeam.setTag(dto.getTag());
        savedTeam.setType(teamTypeEntityOf(TeamType.TEAM));
        savedTeam.setCaptainId(1L);
        savedTeam.setRating(750);
        when(teamRepository.save(any())).thenReturn(savedTeam);

        teamService.createTeam(dto, 1L);

        verify(teamRepository).save(argThat(t ->
            "Testovaci tym".equals(t.getName()) &&
            "TST".equals(t.getTag()) &&
            t.getType() != null &&
            Long.valueOf(1L).equals(t.getCaptainId()) &&
            t.getRating() == 750
        ));
    }

    @Test
    void createTeam_validniData_vratiIdAZpravu() {
        CreateTeamDto dto = createDto();
        when(teamRepository.existsByName(dto.getName())).thenReturn(false);
        when(teamTypeRepository.findByCode(TeamType.TEAM)).thenReturn(Optional.of(teamTypeEntityOf(TeamType.TEAM)));
        when(userFacade.getCaptainRating(1L)).thenReturn(500);

        Team savedTeam = new Team();
        savedTeam.setName(dto.getName());
        savedTeam.setTag(dto.getTag());
        savedTeam.setType(teamTypeEntityOf(TeamType.TEAM));
        savedTeam.setCaptainId(1L);
        savedTeam.setRating(500);
        when(teamRepository.save(any())).thenAnswer(inv -> {
            Team t = inv.getArgument(0);
            t.setTeamId(42L);
            return t;
        });

        CreateTeamResponse response = teamService.createTeam(dto, 1L);

        assertThat(response.getTeamId()).isEqualTo(42L);
        assertThat(response.getMessage()).isNotNull();
    }
}
