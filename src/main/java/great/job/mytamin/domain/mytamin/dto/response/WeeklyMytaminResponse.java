package great.job.mytamin.domain.mytamin.dto.response;

import great.job.mytamin.domain.mytamin.entity.Mytamin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyMytaminResponse {

    Long mytaminId;
    String takeAt;
    ReportResponse report;
    CareResponse care;

    public static WeeklyMytaminResponse of(Mytamin mytamin,
                                           ReportResponse reportResponse,
                                           CareResponse careResponse) {
        LocalDateTime target = mytamin.getTakeAt();
        return WeeklyMytaminResponse.builder()
                .mytaminId(mytamin.getMytaminId())
                .takeAt(target.getMonth().getValue() + "월 " + target.getDayOfMonth() + "일의 마이타민")
                .report(reportResponse)
                .care(careResponse)
                .build();
    }

}
