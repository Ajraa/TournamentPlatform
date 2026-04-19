package cz.ajraa.tournament.user.internal;

import cz.ajraa.tournament.common.exceptions.IllegalUpdateException;
import cz.ajraa.tournament.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;


    public UserDto me(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Uživatel s ID %s neexistuje".formatted(userId)));

        return mapUserToDto(user, true);
    }

    @Transactional
    public UserDto updateUser(Long userId, UpdateUserDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Uživatel s ID %s neexistuje".formatted(userId)));

        boolean isFounder = user.getRoles().stream()
                .anyMatch(role -> role.getName() == RoleType.FOUNDER);

        if (isFounder)
        {
            if (isBlank(dto.getFirstName())) throw new IllegalUpdateException("firstName", "Zakladatel turnajů musí mít vyplněné jméno.");
            if (isBlank(dto.getLastName())) throw new IllegalUpdateException("lastName", "Zakladatel turnajů musí mít vyplněné příjmení.");
            if (isBlank(dto.getBankNumber())) throw new IllegalUpdateException("bankNumber", "Zakladatel turnajů musí mít vyplněný bankovní účet.");
            if (isBlank(dto.getStreet())) throw new IllegalUpdateException("street", "Zakladatel turnajů musí mít vyplněnou ulici.");
            if (isBlank(dto.getCity())) throw new IllegalUpdateException("city", "Zakladatel turnajů musí mít vyplněné město.");
            if (isBlank(dto.getPostcode())) throw new IllegalUpdateException("postcode", "Zakladatel turnajů musí mít vyplněné PSČ.");
            if (isBlank(dto.getCountry())) throw new IllegalUpdateException("country", "Zakladatel turnajů musí mít vyplněnou zemi.");
            if (isBlank(dto.getHouseNumber())) throw new IllegalUpdateException("houseNumber", "Zakladatel turnajů musí mít vyplněné číslo domu.");
        }

        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setStreet(dto.getStreet());
        user.setCity(dto.getCity());
        user.setPostcode(dto.getPostcode());
        user.setCountry(dto.getCountry());
        user.setHouseNumber(dto.getHouseNumber());
        user.setBankNumber(dto.getBankNumber());

        User updatedUser = userRepository.save(user);

        return mapUserToDto(updatedUser, true);
    }

    @Transactional
    public void changePassword(Long userId, ChangePasswordDto dto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Uživatel s ID %s neexistuje".formatted(userId)));

        if (!passwordEncoder.matches(dto.getOldPassword(), user.getPasswordHash()))
            throw new BadCredentialsException("Staré heslo se neshoduje se současným heslem.");

        user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
        userRepository.save(user);
    }

    private boolean isBlank(String value) {
        return value == null || value.trim().isEmpty();
    }

    private UserDto mapUserToDto(User user, boolean isMe)
    {
        UserDto dto = new UserDto();
        dto.setUserId(user.getUserId());
        dto.setNickname(user.getNickname());
        dto.setWinrate(user.getWinrate());
        dto.setRating(user.getRating());
        dto.setRoles(user.getRoles().stream().map(Role::getName).collect(Collectors.toSet()));

        if (isMe)
        {
            dto.setEmail(user.getEmail());
            dto.setFirstName(user.getFirstName());
            dto.setLastName(user.getLastName());
            dto.setCity(user.getCity());
            dto.setStreet(user.getStreet());
            dto.setHouseNumber(user.getHouseNumber());
            dto.setPostcode(user.getPostcode());
            dto.setBankNumber(user.getBankNumber());
        }

        return dto;
    }
}
