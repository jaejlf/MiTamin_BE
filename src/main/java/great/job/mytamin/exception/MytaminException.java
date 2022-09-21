package great.job.mytamin.exception;

import great.job.mytamin.dto.response.ErrorResponse;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MytaminException extends RuntimeException {

    private final HttpStatus status;
    private final ErrorResponse errorResponse;

    public MytaminException(ErrorMap errorMap) {
        this.status = errorMap.getHttpStatus();
        this.errorResponse = ErrorResponse.of(errorMap.getHttpStatus().value(), errorMap.getErrorName(), errorMap.getMessage());
    }

}