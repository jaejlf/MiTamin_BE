package great.job.mytamin.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.*;

@Getter
@RequiredArgsConstructor
public enum ErrorMap {

    USER_NOT_FOUND(NOT_FOUND, "USER_NOT_FOUND", "존재하지 않는 유저입니다."),
    PASSWORD_ERROR(BAD_REQUEST, "PASSWORD_ERROR", "잘못된 비밀번호입니다.");

    private final HttpStatus httpStatus;
    private final String errorName;
    private final String message;

}
