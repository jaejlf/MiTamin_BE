package great.job.mytamin.domain.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MydayResponse {

    String myDayMMDD;
    String dday;
    String msg;

    public static MydayResponse of(String myDayMMDD, String dday, String msg) {
        return MydayResponse.builder()
                .myDayMMDD(myDayMMDD)
                .dday(dday)
                .msg(msg)
                .build();
    }

}
