package cz.ajraa.tournament.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class UserController {

    private final UserService userService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> RegisterUser(@Valid @RequestBody UserRegistrationDto dto) {
        AuthResponseDto authDto = userService.RegisterUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(authDto);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> LoginUser(@Valid @RequestBody LoginDto dto) {
        AuthResponseDto responseDto = userService.LoginUser(dto);
        return ResponseEntity.ok().body(responseDto);
    }
}
