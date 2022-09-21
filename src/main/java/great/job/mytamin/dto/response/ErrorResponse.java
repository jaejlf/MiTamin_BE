package great.job.mytamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private int statusCode;
    private String errorName;
    private String message;

    public static ErrorResponse of(int statusCode, String errorName, String message) {
        return ErrorResponse.builder()
                .statusCode(statusCode)
                .errorName(errorName)
                .message(message)
                .build();
    }

}
