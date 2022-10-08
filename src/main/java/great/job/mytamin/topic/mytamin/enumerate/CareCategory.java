package great.job.mytamin.topic.mytamin.enumerate;


import great.job.mytamin.global.exception.MytaminException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static great.job.mytamin.global.exception.ErrorMap.INVALID_CATEGORY_CODE_ERROR;

@Getter
@RequiredArgsConstructor
public enum CareCategory {

    ACCOMPLISHED(1, "이루어 낸 일"),
    DID_WELL(2, "잘한 일이나 행동"),
    TRYING(3, "노력하고 있는 부분"),
    POSITIVE_CHANGE(4, "긍정적인 변화나 깨달음"),
    THOUGHTS(5, "감정, 생각 혹은 신체 일부분"),
    ME_FROM_THE_PAST(6, "과거의 나"),
    ETC(7, "기타");

    private final int code;
    private final String msg;

    public static String getMsgToCode(int code) {
        if (code == 1) return ACCOMPLISHED.getMsg();
        else if (code == 2) return DID_WELL.getMsg();
        else if (code == 3) return TRYING.getMsg();
        else if (code == 4) return POSITIVE_CHANGE.getMsg();
        else if (code == 5) return THOUGHTS.getMsg();
        else if (code == 6) return ME_FROM_THE_PAST.getMsg();
        else if (code == 7) return ETC.getMsg();
        else throw new MytaminException(INVALID_CATEGORY_CODE_ERROR);
    }

}
