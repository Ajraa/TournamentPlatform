package cz.ajraa.tournament.common.exceptions;

import lombok.Getter;

@Getter
public class ResourceExistsException extends RuntimeException {
    private final String field;

    public ResourceExistsException(String field, String message) {
        super(message);
        this.field = field;
    }
}
