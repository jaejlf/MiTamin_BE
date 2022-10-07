package great.job.mytamin.domain.mytamin.dto.response;

import great.job.mytamin.domain.care.dto.response.CareResponse;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.report.dto.response.ReportResponse;
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