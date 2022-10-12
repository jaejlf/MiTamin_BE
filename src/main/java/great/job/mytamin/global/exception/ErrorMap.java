package great.job.mytamin.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@RequiredArgsConstructor
public enum ErrorMap {

    // JWT 1xxx
    EXPIRED_TOKEN_ERROR(HttpStatus.UNAUTHORIZED, 1000, "EXPIRED_TOKEN_ERROR", "만료된 액세스 토큰입니다."),
    INVALID_TOKEN_ERROR(HttpStatus.FORBIDDEN, 1001, "INVALID_TOKEN_ERROR", "잘못된 토큰입니다."),

    // Auth 2xxx
    EMAIL_PATTERN_ERROR(HttpStatus.BAD_REQUEST, 2000, "EMAIL_PATTERN_ERROR", "잘못된 형식의 이메일입니다."),
    PASSWORD_PATTERN_ERROR(HttpStatus.BAD_REQUEST, 2001, "PASSWORD_PATTERN_ERROR", "비밀번호는 영문, 숫자를 포함한 8 ~ 30자리여야 합니다."),
    USER_ALREADY_EXIST_ERROR(HttpStatus.CONFLICT, 2002, "USER_ALREADY_EXIST_ERROR", "이미 가입된 유저입니다."),
    NICKNAME_DUPLICATE_ERROR(HttpStatus.CONFLICT, 2003, "NICKNAME_DUPLICATE_ERROR", "이미 사용 중인 닉네임입니다."),
    NO_AUTH_ERROR(HttpStatus.FORBIDDEN, 2004, "NO_AUTH_ERROR", "수정/삭제 권한이 없습니다."),

    // User 3xxx
    USER_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, 3000, "USER_NOT_FOUND_ERROR", "존재하지 않는 유저입니다."),
    PASSWORD_MISMATCH_ERROR(HttpStatus.BAD_REQUEST, 3001, "PASSWORD_MISMATCH_ERROR", "잘못된 비밀번호입니다."),

    // Report 4xxx
    INVALID_CONDITION_CODE_ERROR(HttpStatus.BAD_REQUEST, 4000, "INVALID_CONDITION_CODE_ERROR", "마음 컨디션 코드는 1 ~ 5 사이의 값이어야 합니다."),
    REPORT_ALREADY_DONE(HttpStatus.CONFLICT, 4001, "REPORT_ALREADY_DONE", "오늘의 '하루 진단하기'를 이미 완료했습니다."),
    REPORT_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, 4002, "REPORT_NOT_FOUND_ERROR", "존재하지 않는 reportId 입니다."),

    // Care 5xxx
    INVALID_CATEGORY_CODE_ERROR(HttpStatus.BAD_REQUEST, 5000, "INVALID_CATEGORY_CODE_ERROR", "카테고리 코드는 1 ~ 7 사이의 값이어야 합니다."),
    CARE_ALREADY_DONE(HttpStatus.CONFLICT, 5001, "CARE_ALREADY_DONE", "오늘의 '칭찬 처방하기'를 이미 완료했습니다."),
    CARE_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, 5002, "CARE_NOT_FOUND_ERROR", "존재하지 않는 careId 입니다."),

    // S3 6xxx
    FILE_UPLOAD_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, 6000, "FILE_UPLOAD_ERROR", "파일 업로드에 실패했습니다."),
    FILE_EXTENTION_ERROR(HttpStatus.BAD_REQUEST, 6001, "FILE_EXTENTION_ERROR", ".jpg, .png, .jpeg 파일만 업로드할 수 있습니다."),

    // Time 7xxx
    EDIT_TIMEOUT_ERROR(HttpStatus.BAD_REQUEST, 7000, "EDIT_TIMEOUT_ERROR", "수정 가능 시간이 아닙니다."),

    // Myday 8xxx
    WISH_NOT_FOUND_ERROR(HttpStatus.NOT_FOUND, 8000, "WISH_NOT_FOUND_ERROR", "존재하지 않는 wishId 입니다.");

    private final HttpStatus httpStatus;
    private final int errorCode;
    private final String errorName;
    private final String message;

}
