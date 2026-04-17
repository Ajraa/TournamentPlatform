package cz.ajraa.tournament.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
public class AuthResponseDto {
    private Long userId;
    private String message;
    private String token; // JEN ZATÍM, PŘESUNOUT DO COOKIE

    public AuthResponseDto(Long userId, String message) {
        this.userId = userId;
        this.message = message;
    }

    public AuthResponseDto(Long userId, String token, String message) {
        this.userId = userId;
        this.token = token;
        this.message = message;
    }
}
