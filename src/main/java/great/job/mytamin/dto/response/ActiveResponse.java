package great.job.mytamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveResponse {

    boolean breathIsDone;
    boolean senseIsDone;
    boolean reportIsDone;
    boolean careIsDone;

    public static ActiveResponse of(boolean breathIsDone, boolean senseIsDone, boolean reportIsDone, boolean careIsDone) {
        return ActiveResponse.builder()
                .breathIsDone(breathIsDone)
                .senseIsDone(senseIsDone)
                .reportIsDone(reportIsDone)
                .careIsDone(careIsDone)
                .build();
    }

}
