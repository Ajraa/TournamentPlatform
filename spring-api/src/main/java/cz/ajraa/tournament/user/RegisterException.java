package cz.ajraa.tournament.user;

import lombok.Getter;

@Getter
public class RegisterException extends RuntimeException {
    private final String field;
    public RegisterException(String field, String message) {
        super(message);
        this.field = field;
    }
}
