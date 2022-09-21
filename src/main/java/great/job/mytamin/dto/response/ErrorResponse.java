package great.job.mytamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ErrorResponse {

    private int statusCode;
    private String errName;
    private String message;

    public static ErrorResponse error(int statusCode, String errName, String message) {
        return ErrorResponse.builder()
                .statusCode(statusCode)
                .errName(errName)
                .message(message)
                .build();
    }

}
