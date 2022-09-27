package great.job.mytamin.domain.report.dto.response;

import great.job.mytamin.domain.report.entity.Report;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    String takeAt;
    String mentalCondition;
    String feelingTag;
    String todayReport;

    public static ReportResponse of(Report report) {
        return ReportResponse.builder()
                .takeAt(report.getMytamin().getTakeAt())
                .mentalCondition(report.getMentalCondition())
                .feelingTag(report.getFeelingTag())
                .todayReport(report.getTodayReport())
                .build();
    }

}
