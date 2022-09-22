package great.job.mytamin.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMap {

    // Auth
    EXPIRED_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, "EXPIRED_TOKEN_ERROR", "만료된 액세스 토큰입니다."),
    INVALID_TOKEN_ERROR(HttpStatus.FORBIDDEN, "INVALID_TOKEN_ERROR", "잘못된 토큰입니다."),

    // User
    USER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, "USER_NOT_FOUND_ERROR", "존재하지 않는 유저입니다."),
    PASSWORD_MISMATCH_ERROR(HttpStatus.BAD_REQUEST, "PASSWORD_MISMATCH_ERROR", "잘못된 비밀번호입니다.");

    private final HttpStatus httpStatus;
    private final String errorName;
    private final String message;

}
