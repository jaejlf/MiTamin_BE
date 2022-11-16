package great.job.mytamin.domain.myday.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MydayMessage {

    BEFORE_MYDAY("이번 마이데이에는 무엇을 해볼까요 ?"),
    SOON_MYDAY("두근두근 ! 마이데이가 머지 않았어요"),
    THE_DAY_OF_MYDAY("기분 좋은 마이데이 되세요 !"),
    AFTER_MYDAY("이번 마이데이는 어떤 하루였나요 ?");

    private final String msg;

}
