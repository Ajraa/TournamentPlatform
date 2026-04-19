package cz.ajraa.tournament.user.internal;

import com.fasterxml.jackson.databind.ObjectMapper;
import cz.ajraa.tournament.common.exceptions.GlobalExceptionHandler;
import cz.ajraa.tournament.common.exceptions.IllegalUpdateException;
import cz.ajraa.tournament.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserControllerTest {

    private static final String ME_URL = "/api/v1/users/me";
    private static final String PASSWORD_URL = "/api/v1/users/me/password";
    private static final Long USER_ID = 1L;

    @Mock
    private UserService userService;

    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private Authentication auth;

    @BeforeEach
    void setUp() {
        auth = mock(Authentication.class);
        when(auth.getName()).thenReturn(USER_ID.toString());

        mockMvc = MockMvcBuilders
            .standaloneSetup(new UserController(userService))
            .setControllerAdvice(new UserExceptionHandler(), new GlobalExceptionHandler())
            .build();
    }

    // ─── helpers ──────────────────────────────────────────────────────────────

    private UserDto playerUserDto() {
        UserDto dto = new UserDto();
        dto.setUserId(USER_ID);
        dto.setNickname("hrac1");
        dto.setEmail("hrac@example.com");
        dto.setRating(500);
        dto.setRoles(Set.of(RoleType.PLAYER));
        return dto;
    }

    private UpdateUserDto validUpdateDto() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setFirstName("Jan");
        dto.setLastName("Novák");
        return dto;
    }

    private ChangePasswordDto validChangePasswordDto() {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword("stareheslo1");
        dto.setNewPassword("noveheslo1");
        return dto;
    }

    // ─── GET /me ──────────────────────────────────────────────────────────────

    @Test
    void me_autentizovanyUzivatel_vrati200SUserDto() throws Exception {
        when(userService.me(USER_ID)).thenReturn(playerUserDto());

        mockMvc.perform(get(ME_URL).principal(auth))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(USER_ID))
            .andExpect(jsonPath("$.nickname").value("hrac1"));
    }

    @Test
    void me_neexistujiciUzivatel_vrati404() throws Exception {
        when(userService.me(USER_ID))
            .thenThrow(new ResourceNotFoundException("Uživatel s ID 1 neexistuje"));

        mockMvc.perform(get(ME_URL).principal(auth))
            .andExpect(status().isNotFound());
    }

    // ─── PUT /me ──────────────────────────────────────────────────────────────

    @Test
    void putMe_validniRequest_vrati200SUserDto() throws Exception {
        when(userService.updateUser(eq(USER_ID), any())).thenReturn(playerUserDto());

        mockMvc.perform(put(ME_URL).principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdateDto())))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.userId").value(USER_ID));
    }

    @Test
    void putMe_founderBezPovinnehoPole_vrati400SInvalidFields() throws Exception {
        when(userService.updateUser(eq(USER_ID), any()))
            .thenThrow(new IllegalUpdateException("firstName", "Zakladatel turnajů musí mít vyplněné jméno."));

        mockMvc.perform(put(ME_URL).principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validUpdateDto())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.firstName").exists());
    }

    @Test
    void putMe_bezContentType_vrati415() throws Exception {
        mockMvc.perform(put(ME_URL).principal(auth)
                .content(objectMapper.writeValueAsString(validUpdateDto())))
            .andExpect(status().isUnsupportedMediaType());
    }

    // ─── PUT /me/password ─────────────────────────────────────────────────────

    @Test
    void changePassword_validniRequest_vrati204() throws Exception {
        mockMvc.perform(put(PASSWORD_URL).principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validChangePasswordDto())))
            .andExpect(status().isNoContent());
    }

    @Test
    void changePassword_prazdneStareHeslo_vrati400SPolemOldPassword() throws Exception {
        ChangePasswordDto dto = validChangePasswordDto();
        dto.setOldPassword("");

        mockMvc.perform(put(PASSWORD_URL).principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.oldPassword").exists());
    }

    @Test
    void changePassword_prazdneNoveHeslo_vrati400SPolemNewPassword() throws Exception {
        ChangePasswordDto dto = validChangePasswordDto();
        dto.setNewPassword("");

        mockMvc.perform(put(PASSWORD_URL).principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.newPassword").exists());
    }

    @Test
    void changePassword_kratkeNoveHeslo_vrati400SPolemNewPassword() throws Exception {
        ChangePasswordDto dto = validChangePasswordDto();
        dto.setNewPassword("1234567"); // 7 znaků, minimum je 8

        mockMvc.perform(put(PASSWORD_URL).principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(dto)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.invalidFields.newPassword").exists());
    }

    @Test
    void changePassword_spatneStareHeslo_vrati401() throws Exception {
        doThrow(new BadCredentialsException("Staré heslo se neshoduje se současným heslem."))
            .when(userService).changePassword(eq(USER_ID), any());

        mockMvc.perform(put(PASSWORD_URL).principal(auth)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(validChangePasswordDto())))
            .andExpect(status().isUnauthorized())
            .andExpect(jsonPath("$.error").value("Staré heslo se neshoduje se současným heslem."));
    }

    @Test
    void changePassword_bezContentType_vrati415() throws Exception {
        mockMvc.perform(put(PASSWORD_URL).principal(auth)
                .content(objectMapper.writeValueAsString(validChangePasswordDto())))
            .andExpect(status().isUnsupportedMediaType());
    }
}
