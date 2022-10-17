package great.job.mytamin.domain.myday.dto.response;

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
    String comment;

    public static MydayResponse of(String myDayMMDD, String dday, String comment) {
        return MydayResponse.builder()
                .myDayMMDD(myDayMMDD)
                .dday(dday)
                .comment(comment)
                .build();
    }

}
