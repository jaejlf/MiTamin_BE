package great.job.mytamin.domain.myday.enumerate;

import great.job.mytamin.global.exception.MytaminException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static great.job.mytamin.global.exception.ErrorMap.INVALID_MYDAY_ALARM_CODE_ERROR;

@Getter
@RequiredArgsConstructor
public enum MydayAlarm {

    NONE(0, "없음"),
    TODAY(1, "당일"),
    DAY_AGO(2, "하루 전"),
    WEEK_AGO(3, "일주일 전");

    private final int code;
    private final String msg;

    public static String convertCodeToMsg(int code) {
        if (code == 0) return NONE.getMsg();
        else if (code == 1) return TODAY.getMsg();
        else if (code == 2) return DAY_AGO.getMsg();
        else if (code == 3) return WEEK_AGO.getMsg();
        else throw new MytaminException(INVALID_MYDAY_ALARM_CODE_ERROR);
    }

}
