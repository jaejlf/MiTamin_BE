package great.job.mytamin.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum MentalCondition {

    VERY_BAD(1, "매우 나빠요.."),
    BAD(2, "나쁜 편이에요 :("),
    SO_SO(3, "그럭저럭이에요"),
    GOOD(4, "좋은 편이에요 :)"),
    VERY_GOOD(5, "매우 좋아요 !");

    private final int code;
    private final String message;

}
