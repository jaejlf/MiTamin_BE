package great.job.mytamin.global.exception;

import great.job.mytamin.global.dto.response.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.format.DateTimeParseException;

import static great.job.mytamin.global.exception.ErrorMap.DATETIME_PARSE_ERROR;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> defaultExceptionHandler(Exception e) {
        log.error("[" + e.getClass().getSimpleName() + "] " + e.getMessage());
        return ResponseEntity
                .status(INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.of(INTERNAL_SERVER_ERROR.value(), 9999, e.getClass().getSimpleName(), e.getMessage()));
    }

    @ExceptionHandler(MytaminException.class)
    public ResponseEntity<Object> mytaminExceptionHandler(MytaminException e) {
        log.error("[" + e.getClass().getSimpleName() + "] " + e.getErrorResponse().getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(e.getErrorResponse());
    }

    @ExceptionHandler(DateTimeParseException.class)
    public ResponseEntity<Object> dateTimeParseExceptionHandler() {
        MytaminException e = new MytaminException(DATETIME_PARSE_ERROR);
        log.error("[" + e.getClass().getSimpleName() + "] " + e.getErrorResponse().getMessage());
        return ResponseEntity
                .status(e.getStatus())
                .body(e.getErrorResponse());
    }

}
