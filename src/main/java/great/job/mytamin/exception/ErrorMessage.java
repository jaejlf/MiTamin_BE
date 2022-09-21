package great.job.mytamin.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ErrorMessage {

    USER_NOT_FOUND("존재하지 않는 유저입니다.");

    private final String msg;

}
