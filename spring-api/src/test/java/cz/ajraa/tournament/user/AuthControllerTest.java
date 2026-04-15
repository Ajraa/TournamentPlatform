package cz.ajraa.tournament.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.ajraa.tournament.common.exceptions.GlobalExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.security.authentication.BadCredentialsException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    private static final String REGISTER_URL = "/api/v1/auth/register";
    private static final String LOGIN_URL = "/api/v1/auth/login";

    @Mock
    private AuthService authService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
            .standaloneSetup(new AuthController(authService))
            .setControllerAdvice(new UserExceptionHandler(), new GlobalExceptionHandler())
            .build();
    }

    // ─── helpers ───────────────────────────────────────────────────────────────

    private UserRegistrationDto validPlayerDto() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("hrac@example.com");
        dto.setPassword("heslo123");
        dto.setNickname("hrac1");
        dto.setRole(RoleType.PLAYER);
        return dto;
    }

    private UserRegistrationDto validFounderDto() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("zakladatel@example.com");
        dto.setPassword("heslo123");
        dto.setNickname("zakladatel1");
        dto.setRole(RoleType.FOUNDER);
        dto.setFirstName("Jan");
        dto.setLastName("Novák");
        dto.setBankAccount("123456789/0100");
        dto.setStreet("Hlavní");
        dto.setHouseNumber("1");
        dto.setCity("Praha");
        dto.setPostcode("10000");
        dto.setCountry("CZ");
        return dto;
    }

    // ─── úspěšná registrace ─────────────────────────────────────────────────

    @Test
    void registerPlayer_validniRequest_vrati201STelemem() throws Exception {
        when(authService.RegisterUser(any()))
            .thenReturn(new AuthResponseDto(1L, "Registrace proběhla úspěšně"));

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPlayerDto())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value(1L))
            .andExpect(jsonPath("$.message").value("Registrace proběhla úspěšně"));
    }

    @Test
    void registerFounder_validniRequest_vrati201() throws Exception {
        when(authService.RegisterUser(any()))
            .thenReturn(new AuthResponseDto(2L, "Registrace proběhla úspěšně"));

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validFounderDto())))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.userId").value(2L));
    }

    // ─── validace základních polí – 400 ─────────────────────────────────────

    @Test
    void register_prazdnyEmail_vrati400SPolemEmail() throws Exception {
        UserRegistrationDto dto = validPlayerDto();
        dto.setEmail("");

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.email").exists());
    }

    @Test
    void register_spatnyFormatEmailu_vrati400SPolemEmail() throws Exception {
        UserRegistrationDto dto = validPlayerDto();
        dto.setEmail("neni-email");

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.email").exists());
    }

    @Test
    void register_kratkeHeslo_vrati400SPolemPassword() throws Exception {
        UserRegistrationDto dto = validPlayerDto();
        dto.setPassword("1234567"); // 7 znaků

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.password").exists());
    }

    @Test
    void register_prazdneNickname_vrati400SPolemNickname() throws Exception {
        UserRegistrationDto dto = validPlayerDto();
        dto.setNickname("");

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.nickname").exists());
    }

    // ─── validace FOUNDER polí – 400 ────────────────────────────────────────

    @Test
    void register_founderBezZakladatelskychPoli_vrati400() throws Exception {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("zakladatel@example.com");
        dto.setPassword("heslo123");
        dto.setNickname("zakladatel1");
        dto.setRole(RoleType.FOUNDER);
        // žádné zakladatelské pole

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.firstName").exists())
            .andExpect(jsonPath("$.invalidFields.lastName").exists())
            .andExpect(jsonPath("$.invalidFields.bankAccount").exists())
            .andExpect(jsonPath("$.invalidFields.city").exists())
            .andExpect(jsonPath("$.invalidFields.street").exists());
    }

    // ─── duplicity – 409 ────────────────────────────────────────────────────

    @Test
    void register_duplicitniEmail_vrati409SPolemEmail() throws Exception {
        when(authService.RegisterUser(any()))
            .thenThrow(new RegisterException("email", "Uživatel s tímto emailem již existuje."));

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPlayerDto())))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.invalidFields.email").value("Uživatel s tímto emailem již existuje."));
    }

    @Test
    void register_duplicitniNickname_vrati409SPolemNickname() throws Exception {
        when(authService.RegisterUser(any()))
            .thenThrow(new RegisterException("nickname", "Uživatel s tímto uživatelským jménem již existuje."));

        mockMvc.perform(post(REGISTER_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validPlayerDto())))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.invalidFields.nickname").value("Uživatel s tímto uživatelským jménem již existuje."));
    }

    // ─── content-type ────────────────────────────────────────────────────────

    @Test
    void register_bezContentType_vrati415() throws Exception {
        mockMvc.perform(post(REGISTER_URL)
                .content(objectMapper.writeValueAsString(validPlayerDto())))
            .andExpect(status().isUnsupportedMediaType());
    }

    // ─── login helpers ─────────────────────────────────────────────────────

    private LoginDto validLoginDto() {
        LoginDto dto = new LoginDto();
        dto.setNickname("hrac1");
        dto.setPassword("heslo123");
        return dto;
    }

    // ─── login ─────────────────────────────────────────────────────────────

    @Test
    void login_validniRequest_vrati200STokenem() throws Exception {
        when(authService.LoginUser(any()))
            .thenReturn(new AuthResponseDto("jwt-token-xyz", "Uživatel přihlášen."));

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginDto())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("jwt-token-xyz"))
            .andExpect(jsonPath("$.message").value("Uživatel přihlášen."));
    }

    // ─── login chyby ───────────────────────────────────────────────────────

    @Test
    void login_spatneUdaje_vrati401SChybou() throws Exception {
        when(authService.LoginUser(any()))
            .thenThrow(new BadCredentialsException("Špatné jméno nebo heslo."));

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validLoginDto())))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Špatné jméno nebo heslo."));
    }

    @Test
    void login_bezContentType_vrati415() throws Exception {
        mockMvc.perform(post(LOGIN_URL)
                .content(objectMapper.writeValueAsString(validLoginDto())))
            .andExpect(status().isUnsupportedMediaType());
    }

    // ─── login validace – 400 ──────────────────────────────────────────────

    @Test
    void login_prazdneNickname_vrati400SPolemNickname() throws Exception {
        LoginDto dto = validLoginDto();
        dto.setNickname("");

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.nickname").exists());
    }

    @Test
    void login_prazdneHeslo_vrati400SPolemPassword() throws Exception {
        LoginDto dto = validLoginDto();
        dto.setPassword("");

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.password").exists());
    }
}
