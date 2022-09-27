package great.job.mytamin.enumerate;

import great.job.mytamin.exception.MytaminException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static great.job.mytamin.exception.ErrorMap.INVALID_CONDITION_CODE_ERROR;

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

    public static String getMsgToCode(int code) {
        if (code == 1) return VERY_BAD.getMsg();
        else if (code == 2) return BAD.getMsg();
        else if (code == 3) return SO_SO.getMsg();
        else if (code == 4) return GOOD.getMsg();
        else if (code == 5) return VERY_GOOD.getMsg();
        else throw new MytaminException(INVALID_CONDITION_CODE_ERROR);
    }

    public static int getCodeToMsg(String msg) {
        if (msg.equals(VERY_BAD.getMsg())) return VERY_BAD.getCode();
        else if (msg.equals(BAD.getMsg())) return BAD.getCode();
        else if (msg.equals(SO_SO.getMsg())) return SO_SO.getCode();
        else if (msg.equals(GOOD.getMsg())) return GOOD.getCode();
        else return VERY_GOOD.getCode();
    }

}
