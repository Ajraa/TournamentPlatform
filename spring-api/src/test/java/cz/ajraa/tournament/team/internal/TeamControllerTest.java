package cz.ajraa.tournament.team.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.ajraa.tournament.common.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class TeamControllerTest {

    private static final String CREATE_URL = "/api/v1/team/";
    private static final Long USER_ID = 1L;

    @Mock
    private TeamService teamService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Authentication auth;

    @BeforeEach
    void setUp() {
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(USER_ID.toString());

        mockMvc = MockMvcBuilders
            .standaloneSetup(new TeamController(teamService))
            .setControllerAdvice(new GlobalExceptionHandler())
            .build();
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private CreateTeamDto validDto() {
        CreateTeamDto dto = new CreateTeamDto();
        dto.setName("Testovaci tym");
        dto.setTag("TST");
        dto.setType(TeamType.TEAM);
        return dto;
    }

    // ─── POST /api/v1/team/ — validní ─────────────────────────────────────────

    @Test
    void createTeam_validniRequest_vrati200SResponse() throws Exception {
        when(teamService.createTeam(any(), eq(USER_ID))).thenReturn(new CreateTeamResponse(10L, "Tým byl vytvořen"));

        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validDto()))
                .principal(auth))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.teamId").value(10L))
            .andExpect(jsonPath("$.message").exists());
    }

    // ─── POST /api/v1/team/ — validační chyby (400) ───────────────────────────

    @Test
    void createTeam_bezNazvu_vrati400SPolemName() throws Exception {
        CreateTeamDto dto = validDto();
        dto.setName("");

        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .principal(auth))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.name").exists());
    }

    @Test
    void createTeam_bezTagu_vrati400SPolemTag() throws Exception {
        CreateTeamDto dto = validDto();
        dto.setTag("");

        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .principal(auth))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.tag").exists());
    }

    @Test
    void createTeam_kratkyTag_vrati400SPolemTag() throws Exception {
        CreateTeamDto dto = validDto();
        dto.setTag("AB");

        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .principal(auth))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.tag").exists());
    }

    @Test
    void createTeam_dlouhyTag_vrati400SPolemTag() throws Exception {
        CreateTeamDto dto = validDto();
        dto.setTag("ABCD");

        mockMvc.perform(post(CREATE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto))
                .principal(auth))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.tag").exists());
    }
}
