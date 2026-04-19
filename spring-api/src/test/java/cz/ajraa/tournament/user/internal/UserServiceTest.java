package cz.ajraa.tournament.user.internal;

import cz.ajraa.tournament.common.exceptions.IllegalUpdateException;
import cz.ajraa.tournament.common.exceptions.ResourceNotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    // ─── helpers ───────────────────────────────────────────────────────────────

    private Role roleOf(RoleType type) {
        Role role = new Role();
        role.setName(type);
        return role;
    }

    private User userWithRoles(RoleType... types) {
        User user = new User();
        user.setUserId(1L);
        user.setNickname("hrac1");
        user.setEmail("hrac@example.com");
        user.setPasswordHash("hashedPassword");
        Set<Role> roles = new HashSet<>();
        for (RoleType type : types) {
            roles.add(roleOf(type));
        }
        user.setRoles(roles);
        return user;
    }

    private UpdateUserDto founderUpdateDto() {
        UpdateUserDto dto = new UpdateUserDto();
        dto.setFirstName("Jan");
        dto.setLastName("Novák");
        dto.setBankNumber("123456789/0100");
        dto.setStreet("Hlavní");
        dto.setHouseNumber("1");
        dto.setCity("Praha");
        dto.setPostcode("10000");
        dto.setCountry("CZ");
        return dto;
    }

    private ChangePasswordDto changePasswordDto(String oldPassword, String newPassword) {
        ChangePasswordDto dto = new ChangePasswordDto();
        dto.setOldPassword(oldPassword);
        dto.setNewPassword(newPassword);
        return dto;
    }

    // ─── me() ─────────────────────────────────────────────────────────────────

    @Test
    void me_existujiciUzivatel_vratiUserDto() {
        User user = userWithRoles(RoleType.PLAYER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UserDto result = userService.me(1L);

        assertThat(result.getUserId()).isEqualTo(1L);
        assertThat(result.getNickname()).isEqualTo("hrac1");
        assertThat(result.getEmail()).isEqualTo("hrac@example.com");
    }

    @Test
    void me_neexistujiciUzivatel_hodiResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.me(99L))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── updateUser() ─────────────────────────────────────────────────────────

    @Test
    void updateUser_player_ulozi() {
        User user = userWithRoles(RoleType.PLAYER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        UpdateUserDto dto = new UpdateUserDto();
        dto.setFirstName("Jan");
        dto.setLastName("Novák");

        userService.updateUser(1L, dto);

        verify(userRepository).save(argThat(u ->
            "Jan".equals(u.getFirstName()) && "Novák".equals(u.getLastName())
        ));
    }

    @Test
    void updateUser_playerBezPoli_uloziNullHodnoty() {
        User user = userWithRoles(RoleType.PLAYER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        userService.updateUser(1L, new UpdateUserDto()); // vše null

        verify(userRepository).save(any());
    }

    @Test
    void updateUser_founderSeVsemiPoli_ulozi() {
        User user = userWithRoles(RoleType.FOUNDER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenReturn(user);

        userService.updateUser(1L, founderUpdateDto());

        verify(userRepository).save(argThat(u ->
            "Jan".equals(u.getFirstName()) &&
            "Novák".equals(u.getLastName()) &&
            "123456789/0100".equals(u.getBankNumber()) &&
            "Hlavní".equals(u.getStreet()) &&
            "Praha".equals(u.getCity())
        ));
    }

    @Test
    void updateUser_founderBezJmena_hodiIllegalUpdateExceptionProFirstName() {
        User user = userWithRoles(RoleType.FOUNDER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UpdateUserDto dto = founderUpdateDto();
        dto.setFirstName(null);

        assertThatThrownBy(() -> userService.updateUser(1L, dto))
            .isInstanceOf(IllegalUpdateException.class)
            .satisfies(ex -> assertThat(((IllegalUpdateException) ex).getField()).isEqualTo("firstName"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_founderBezPrijmeni_hodiIllegalUpdateExceptionProLastName() {
        User user = userWithRoles(RoleType.FOUNDER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UpdateUserDto dto = founderUpdateDto();
        dto.setLastName(null);

        assertThatThrownBy(() -> userService.updateUser(1L, dto))
            .isInstanceOf(IllegalUpdateException.class)
            .satisfies(ex -> assertThat(((IllegalUpdateException) ex).getField()).isEqualTo("lastName"));

        verify(userRepository, never()).save(any());
    }

    @Test
    void updateUser_founderSBilymZnakemVJmenu_hodiIllegalUpdateExceptionProFirstName() {
        User user = userWithRoles(RoleType.FOUNDER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UpdateUserDto dto = founderUpdateDto();
        dto.setFirstName("   "); // jen mezery

        assertThatThrownBy(() -> userService.updateUser(1L, dto))
            .isInstanceOf(IllegalUpdateException.class)
            .satisfies(ex -> assertThat(((IllegalUpdateException) ex).getField()).isEqualTo("firstName"));
    }

    @Test
    void updateUser_founderBezBankovnihoUctu_hodiIllegalUpdateExceptionProBankNumber() {
        User user = userWithRoles(RoleType.FOUNDER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        UpdateUserDto dto = founderUpdateDto();
        dto.setBankNumber(null);

        assertThatThrownBy(() -> userService.updateUser(1L, dto))
            .isInstanceOf(IllegalUpdateException.class)
            .satisfies(ex -> assertThat(((IllegalUpdateException) ex).getField()).isEqualTo("bankNumber"));
    }

    @Test
    void updateUser_neexistujiciUzivatel_hodiResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateUser(99L, new UpdateUserDto()))
            .isInstanceOf(ResourceNotFoundException.class);
    }

    // ─── changePassword() ─────────────────────────────────────────────────────

    @Test
    void changePassword_spravneStareHeslo_uloziNovyHash() {
        User user = userWithRoles(RoleType.PLAYER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("stare123", "hashedPassword")).thenReturn(true);
        when(passwordEncoder.encode("nove1234")).thenReturn("novyHash");

        userService.changePassword(1L, changePasswordDto("stare123", "nove1234"));

        verify(userRepository).save(argThat(u -> "novyHash".equals(u.getPasswordHash())));
    }

    @Test
    void changePassword_spatneStareHeslo_hodiBadCredentialsException() {
        User user = userWithRoles(RoleType.PLAYER);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("spatne", "hashedPassword")).thenReturn(false);

        assertThatThrownBy(() -> userService.changePassword(1L, changePasswordDto("spatne", "nove1234")))
            .isInstanceOf(BadCredentialsException.class);

        verify(userRepository, never()).save(any());
    }

    @Test
    void changePassword_neexistujiciUzivatel_hodiResourceNotFoundException() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.changePassword(99L, changePasswordDto("stare123", "nove1234")))
            .isInstanceOf(ResourceNotFoundException.class);
    }
}
