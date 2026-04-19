package cz.ajraa.tournament.user.internal;

import lombok.Getter;

@Getter
class RegisterException extends RuntimeException {
    private final String field;
    public RegisterException(String field, String message) {
        super(message);
        this.field = field;
    }
}
