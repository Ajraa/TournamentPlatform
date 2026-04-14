package cz.ajraa.tournament.user;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice(basePackages = "cz.ajraa.tournament.user;")
public class UserExceptionHandler {

    @ExceptionHandler(RegisterException.class)
    public ProblemDetail HandleRegisterException(RegisterException ex) {
        log.warn("Pokus o registraci selhal: {}", ex.getMessage());

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT,
                "Chyba registrace"
        );

        problemDetail.setProperty("invalidFields", Map.of(ex.getField(), ex.getMessage()));
        return problemDetail;
    }
}
