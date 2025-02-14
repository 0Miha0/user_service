package school.faang.user_service.exception;

import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import school.faang.user_service.exception.customexception.DataValidationException;
import school.faang.user_service.exception.customexception.DiceBearException;

import java.time.LocalDateTime;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({IllegalArgumentException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgumentException(Exception ex) {
        return buildResponse(ex);
    }

    @ExceptionHandler({DataValidationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleDataValidationException(Exception ex) {
        return buildResponse(ex);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleEntityNotFoundException(EntityNotFoundException ex) {
        return buildResponse(ex);
    }

    @ExceptionHandler({DiceBearException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleDiceBearException(DiceBearException ex) {
        return buildResponse(ex);
    }

    private ErrorResponse buildResponse(Exception ex) {
        log.error(ex.getClass().getName(), ex);
        return ErrorResponse.builder()
                .timestamp(LocalDateTime.now())
                .error(ex.getClass().getName())
                .message(Objects.requireNonNullElse(ex.getMessage(), "No message available"))
                .build();
    }
}
