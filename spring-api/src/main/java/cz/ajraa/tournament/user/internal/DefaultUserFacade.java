package cz.ajraa.tournament.user.internal;

import cz.ajraa.tournament.common.exceptions.ResourceNotFoundException;
import cz.ajraa.tournament.user.UserFacade;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DefaultUserFacade implements UserFacade {

    private final UserRepository userRepository;

    @Override
    public int getCaptainRating(Long userId) {
        return userRepository.findById(userId).orElseThrow(() -> new ResourceNotFoundException("Uživatel s ID %s neexistuje".formatted(userId))).getRating();
    }
}
