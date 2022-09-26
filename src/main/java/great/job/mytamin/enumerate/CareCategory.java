package great.job.mytamin.enumerate;


import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
    private final String message;

}
