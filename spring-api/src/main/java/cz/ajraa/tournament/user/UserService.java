package cz.ajraa.tournament.user;

import cz.ajraa.tournament.common.exceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;

    public UserDto me(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Uživatel s ID %s neexistuje".formatted(userId)));

        return mapUserToDto(user, true);
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
            dto.setPostcode(dto.getPostcode());
            dto.setBankNumber(dto.getBankNumber());
        }

        return dto;
    }
}
