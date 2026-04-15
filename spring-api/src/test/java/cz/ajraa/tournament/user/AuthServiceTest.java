package cz.ajraa.tournament.user;

import cz.ajraa.tournament.common.security.JwtService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    // ─── helpers ───────────────────────────────────────────────────────────────

    private UserRegistrationDto playerDto() {
        UserRegistrationDto dto = new UserRegistrationDto();
        dto.setEmail("hrac@example.com");
        dto.setPassword("heslo123");
        dto.setNickname("hrac1");
        dto.setRole(RoleType.PLAYER);
        return dto;
    }

    private UserRegistrationDto founderDto() {
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

    private Role roleOf(RoleType type) {
        Role role = new Role();
        role.setName(type);
        return role;
    }

    private User savedUserWithId(long id) {
        User user = new User();
        user.setUserId(id);
        return user;
    }

    private LoginDto loginDto() {
        LoginDto dto = new LoginDto();
        dto.setNickname("hrac1");
        dto.setPassword("heslo123");
        return dto;
    }

    private User userWithPassword(String nickname, String hash, RoleType... roles) {
        User user = new User();
        user.setUserId(42L);
        user.setNickname(nickname);
        user.setPasswordHash(hash);
        Set<Role> roleSet = new java.util.HashSet<>();
        for (RoleType rt : roles) {
            roleSet.add(roleOf(rt));
        }
        user.setRoles(roleSet);
        return user;
    }

    // ─── úspěšná registrace ─────────────────────────────────────────────────

    @Test
    void registerUser_player_vratiIdAZpravu() {
        UserRegistrationDto dto = playerDto();

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByNickname(dto.getNickname())).thenReturn(false);
        when(roleRepository.findByName(RoleType.PLAYER)).thenReturn(Optional.of(roleOf(RoleType.PLAYER)));
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(savedUserWithId(1L));

        AuthResponseDto result = authService.RegisterUser(dto);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getMessage()).isEqualTo("Registrace proběhla úspěšně");
    }

    @Test
    void registerUser_player_ulozenePolozkySouhlasi() {
        UserRegistrationDto dto = playerDto();

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByNickname(any())).thenReturn(false);
        when(roleRepository.findByName(any())).thenReturn(Optional.of(roleOf(RoleType.PLAYER)));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
        when(userRepository.save(any())).thenReturn(savedUserWithId(1L));

        authService.RegisterUser(dto);

        verify(userRepository).save(argThat(u ->
            "hrac@example.com".equals(u.getEmail()) &&
            "hrac1".equals(u.getNickname()) &&
            "encodedPassword".equals(u.getPasswordHash()) &&
            u.getRating() == 500
        ));
    }

    @Test
    void registerUser_player_nenastavujeZakladatelskePolozky() {
        UserRegistrationDto dto = playerDto();

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByNickname(any())).thenReturn(false);
        when(roleRepository.findByName(any())).thenReturn(Optional.of(roleOf(RoleType.PLAYER)));
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(savedUserWithId(1L));

        authService.RegisterUser(dto);

        verify(userRepository).save(argThat(u ->
            u.getFirstName() == null &&
            u.getLastName() == null &&
            u.getBankNumber() == null &&
            u.getStreet() == null
        ));
    }

    @Test
    void registerUser_founder_nastavujeVsechnyZakladatelskePolozky() {
        UserRegistrationDto dto = founderDto();

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByNickname(any())).thenReturn(false);
        when(roleRepository.findByName(RoleType.FOUNDER)).thenReturn(Optional.of(roleOf(RoleType.FOUNDER)));
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(userRepository.save(any())).thenReturn(savedUserWithId(2L));

        AuthResponseDto result = authService.RegisterUser(dto);

        assertThat(result.getUserId()).isEqualTo(2L);
        verify(userRepository).save(argThat(u ->
            "Jan".equals(u.getFirstName()) &&
            "Novák".equals(u.getLastName()) &&
            "123456789/0100".equals(u.getBankNumber()) &&
            "Hlavní".equals(u.getStreet()) &&
            "1".equals(u.getHouseNumber()) &&
            "Praha".equals(u.getCity()) &&
            "10000".equals(u.getPostcode()) &&
            "CZ".equals(u.getCountry())
        ));
    }

    // ─── duplicity ──────────────────────────────────────────────────────────

    @Test
    void registerUser_duplicitniEmail_hodiRegisterException() {
        UserRegistrationDto dto = playerDto();
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(true);

        assertThatThrownBy(() -> authService.RegisterUser(dto))
            .isInstanceOf(RegisterException.class)
            .hasMessage("Uživatel s tímto emailem již existuje.")
            .satisfies(ex -> assertThat(((RegisterException) ex).getField()).isEqualTo("email"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void registerUser_duplicitniNickname_hodiRegisterException() {
        UserRegistrationDto dto = playerDto();
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userRepository.existsByNickname(dto.getNickname())).thenReturn(true);

        assertThatThrownBy(() -> authService.RegisterUser(dto))
            .isInstanceOf(RegisterException.class)
            .hasMessage("Uživatel s tímto uživatelským jménem již existuje.")
            .satisfies(ex -> assertThat(((RegisterException) ex).getField()).isEqualTo("nickname"));

        verify(userRepository, never()).save(any());
    }

    // ─── chybějící role ─────────────────────────────────────────────────────

    @Test
    void registerUser_roleNeexistujeVDB_hodiIllegalStateException() {
        UserRegistrationDto dto = playerDto();

        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(userRepository.existsByNickname(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("encoded");
        when(roleRepository.findByName(any())).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.RegisterUser(dto))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("PLAYER");

        verify(userRepository, never()).save(any());
    }

    // ─── úspěšný login ─────────────────────────────────────────────────────

    @Test
    void loginUser_spravneUdaje_vratiTokenAZpravu() {
        LoginDto dto = loginDto();
        User user = userWithPassword("hrac1", "hashedPassword", RoleType.PLAYER);

        when(userRepository.findByNickname("hrac1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("heslo123", "hashedPassword")).thenReturn(true);
        when(jwtService.GenerateToken(eq(42L), anyList())).thenReturn("jwt-token-xyz");

        AuthResponseDto result = authService.LoginUser(dto);

        assertThat(result.getToken()).isEqualTo("jwt-token-xyz");
        assertThat(result.getMessage()).isEqualTo("Uživatel přihlášen.");
        verify(jwtService).GenerateToken(eq(42L), argThat(roleNames ->
            roleNames.size() == 1 && roleNames.contains("PLAYER")
        ));
    }

    // ─── login chyby ───────────────────────────────────────────────────────

    @Test
    void loginUser_neexistujiciNickname_hodiBadCredentialsException() {
        LoginDto dto = loginDto();

        when(userRepository.findByNickname("hrac1")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.LoginUser(dto))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("Špatné jméno nebo heslo.");

        verify(passwordEncoder, never()).matches(any(), any());
        verify(jwtService, never()).GenerateToken(any(), any());
    }

    @Test
    void loginUser_spatneHeslo_hodiBadCredentialsException() {
        LoginDto dto = loginDto();
        User user = userWithPassword("hrac1", "hashedPassword", RoleType.PLAYER);

        when(userRepository.findByNickname("hrac1")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("heslo123", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> authService.LoginUser(dto))
            .isInstanceOf(BadCredentialsException.class)
            .hasMessage("Špatné jméno nebo heslo.");

        verify(jwtService, never()).GenerateToken(any(), any());
    }
}
