package cz.ajraa.tournament.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> registerUser(@Valid @RequestBody UserRegistrationDto dto) {
        AuthResponseDto authDto = authService.registerUser(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(authDto);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDto> loginUser(@Valid @RequestBody LoginDto dto) {
        LoginResult loginResult = authService.loginUser(dto);

        ResponseCookie cookie = ResponseCookie.from("jwt", loginResult.token())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(24 * 60 * 60) // 1 den
                .sameSite("Strict")
                .build();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(loginResult.userDto());
    }
}
