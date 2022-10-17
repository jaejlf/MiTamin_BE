package great.job.mytamin.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class NoDataResponse {

    private static final int OK = 200;
    public static final int CREATED = 201;

    private int statusCode;
    private String message;

    public static NoDataResponse ok(String message) {
        return NoDataResponse.builder()
                .statusCode(OK)
                .message(message)
                .build();
    }

    public static NoDataResponse create(String message) {
        return NoDataResponse.builder()
                .statusCode(CREATED)
                .message(message)
                .build();
    }

}
