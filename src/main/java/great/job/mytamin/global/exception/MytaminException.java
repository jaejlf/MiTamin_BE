package great.job.mytamin.global.exception;

import great.job.mytamin.global.dto.response.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MytaminException extends RuntimeException {

    private final HttpStatus status;
    private final String message;
    private final ErrorResponse errorResponse;

    public MytaminException(ErrorMap errorMap) {
        this.status = errorMap.getHttpStatus();
        this.message = errorMap.getErrorName();
        this.errorResponse = ErrorResponse.of(errorMap.getHttpStatus().value(), errorMap.getErrorCode(), errorMap.getErrorName(), errorMap.getMessage());
    }

}