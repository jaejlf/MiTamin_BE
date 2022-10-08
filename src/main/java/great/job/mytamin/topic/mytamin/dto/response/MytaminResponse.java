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
    boolean canEditReport;
    boolean canEditCare;
    ReportResponse report;
    CareResponse care;

    public static MytaminResponse of(Mytamin mytamin,
                                     boolean canEditReport,
                                     boolean canEditCare,
                                     ReportResponse reportResponse,
                                     CareResponse careResponse) {
        return MytaminResponse.builder()
                .takeAt(mytamin.getTakeAt())
                .canEditReport(canEditReport)
                .canEditCare(canEditCare)
                .report(reportResponse)
                .care(careResponse)
                .build();
    }

}