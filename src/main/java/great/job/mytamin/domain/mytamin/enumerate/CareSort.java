package great.job.mytamin.domain.mytamin.enumerate;

import great.job.mytamin.global.exception.MytaminException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import static great.job.mytamin.global.exception.ErrorMap.INVALID_CATEGORY_CODE_ERROR;

@Getter
@RequiredArgsConstructor
public enum CareSort {

    LATEST(1),
    OLDEST(2);
//    BEST(3 ),
//    WORST(4);

    private final int code;

    public static int validateCode(int code) {
        if (code < 1 || code > 4) throw new MytaminException(INVALID_CATEGORY_CODE_ERROR);
        return code;
    }

}
