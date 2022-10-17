package great.job.mytamin.global.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ResultResponse<T> {

    private static final int OK = 200;
    public static final int CREATED = 201;

    private int statusCode;
    private String message;
    private T data;

    public static <T> ResultResponse<Object> ok(String message, T data) {
        return ResultResponse.builder()
                .statusCode(OK)
                .message(message)
                .data(data)
                .build();
    }

    public static <T> ResultResponse<Object> create(String message, T data) {
        return ResultResponse.builder()
                .statusCode(CREATED)
                .message(message)
                .data(data)
                .build();
    }

}
