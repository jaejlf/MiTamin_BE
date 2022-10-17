package great.job.mytamin.domain.home.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActiveResponse {

    Boolean breathIsDone;
    Boolean senseIsDone;
    Boolean reportIsDone;
    Boolean careIsDone;

    public static ActiveResponse of(Boolean breathIsDone, Boolean senseIsDone, Boolean reportIsDone, Boolean careIsDone) {
        return ActiveResponse.builder()
                .breathIsDone(breathIsDone)
                .senseIsDone(senseIsDone)
                .reportIsDone(reportIsDone)
                .careIsDone(careIsDone)
                .build();
    }

}
