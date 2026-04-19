package cz.ajraa.tournament.user;

import cz.ajraa.tournament.common.security.JwtService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
class AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserService userService;

    @Transactional
    AuthResponseDto registerUser(UserRegistrationDto dto) throws RegisterException {
        if (userRepository.existsByEmail(dto.getEmail())) throw new RegisterException("email", "Uživatel s tímto emailem již existuje.");
        if (userRepository.existsByNickname(dto.getNickname())) throw new RegisterException("nickname", "Uživatel s tímto uživatelským jménem již existuje.");

        User user = new User();
        user.setEmail(dto.getEmail());
        user.setNickname(dto.getNickname());

        user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));

        Role userRole = roleRepository.findByName(dto.getRole())
                .orElseThrow(() -> new IllegalStateException("Role %s neexistuje".formatted(dto.getRole())));
        user.setRoles(Set.of(userRole));

        user.setRating(500);

        if (dto.getRole() == RoleType.FOUNDER)
        {
            user.setFirstName(dto.getFirstName());
            user.setLastName(dto.getLastName());
            user.setBankNumber(dto.getBankAccount());

            user.setStreet(dto.getStreet());
            user.setHouseNumber(dto.getHouseNumber());
            user.setCity(dto.getCity());
            user.setPostcode(dto.getPostcode());
            user.setCountry(dto.getCountry());
        }

        User savedUser = userRepository.save(user);
        log.info("Úspěšně zaregistrován nový uživatel s ID: {}", savedUser.getUserId());

        return new AuthResponseDto(savedUser.getUserId(), "Registrace proběhla úspěšně");
    }

    LoginResult loginUser(LoginDto dto) {
        User user = userRepository.findByNickname(dto.getNickname())
                .orElseThrow(() -> new BadCredentialsException("Špatné jméno nebo heslo."));

        if (!passwordEncoder.matches(dto.getPassword(), user.getPasswordHash()))
            throw new BadCredentialsException("Špatné jméno nebo heslo.");

        List<String> roleNames = user.getRoles().stream()
                .map(role -> role.getName().name())
                .toList();
        UserDto userDto = userService.me(user.getUserId());

        String token = jwtService.GenerateToken(user.getUserId(), roleNames);
        return new LoginResult(token, userDto);
    }
}
