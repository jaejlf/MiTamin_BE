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
    String dDay;
    String msg;

    public static MydayResponse of(String myDayMMDD, String dDay, String msg) {
        return MydayResponse.builder()
                .myDayMMDD(myDayMMDD)
                .dDay(dDay)
                .msg(msg)
                .build();
    }

}
