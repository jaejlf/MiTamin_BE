package great.job.mytamin.topic.mytamin.dto.response;

import great.job.mytamin.topic.mytamin.entity.Mytamin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MytaminResponse {

    String takeAt;
    ReportResponse report;
    CareResponse care;

    public static MytaminResponse of(Mytamin mytamin,
                                     ReportResponse reportResponse,
                                     CareResponse careResponse) {
        return MytaminResponse.builder()
                .takeAt(mytamin.getTakeAt())
                .report(reportResponse)
                .care(careResponse)
                .build();
    }

}