package cz.ajraa.tournament.user.internal;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
class UserController {
    private final UserService userService;

    @GetMapping("/me")
    public ResponseEntity<UserDto> me(Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok().body(userService.me(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<UserDto> putMe(@Valid @RequestBody UpdateUserDto dto, Authentication authentication)
    {
        Long userId = Long.parseLong(authentication.getName());
        return ResponseEntity.ok().body(userService.updateUser(userId, dto));
    }

    @PutMapping("/me/password")
    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordDto dto, Authentication authentication) {
        Long userId = Long.parseLong(authentication.getName());
        userService.changePassword(userId, dto);
        return ResponseEntity.noContent().build();
    }
}
