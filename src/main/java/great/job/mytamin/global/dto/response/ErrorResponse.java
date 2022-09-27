package great.job.mytamin.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private int statusCode;
    private int errorCode;
    private String errorName;
    private String message;

    public static ErrorResponse of(int statusCode, int errorCode, String errorName, String message) {
        return ErrorResponse.builder()
                .statusCode(statusCode)
                .errorCode(errorCode)
                .errorName(errorName)
                .message(message)
                .build();
    }

}
