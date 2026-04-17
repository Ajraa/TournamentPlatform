package cz.ajraa.tournament.common.exceptions;

import lombok.Getter;

@Getter
public class IllegalUpdateException extends RuntimeException {
    private final String field;

    public IllegalUpdateException(String field, String message) {
        super(message);
        this.field = field;
    }
}
