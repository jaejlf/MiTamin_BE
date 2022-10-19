package great.job.mytamin.domain.mytamin.enumerate;

import great.job.mytamin.global.exception.MytaminException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static great.job.mytamin.global.exception.ErrorMap.INVALID_CONDITION_CODE_ERROR;

@Getter
@RequiredArgsConstructor
public enum MentalCondition {

    VERY_BAD(1, "매우 나빠요 .."),
    BAD(2, "나쁜 편이에요 :("),
    SO_SO(3, "그럭저럭이에요"),
    GOOD(4, "좋은 편이에요 :)"),
    VERY_GOOD(5, "매우 좋아요 !");

    private final int code;
    private final String msg;

    public static int validateCode(int code) {
        if (code < 1 || code > 5) throw new MytaminException(INVALID_CONDITION_CODE_ERROR);
        return code;
    }

    public static String convertCodeToMsg(int code) {
        if (code == 1) return VERY_BAD.getMsg();
        else if (code == 2) return BAD.getMsg();
        else if (code == 3) return SO_SO.getMsg();
        else if (code == 4) return GOOD.getMsg();
        else return VERY_GOOD.getMsg();
    }

}
