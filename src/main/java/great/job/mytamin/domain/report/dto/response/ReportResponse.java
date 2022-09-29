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
                .feelingTag(getFeelingTag(report))
                .todayReport(report.getTodayReport())
                .build();
    }

    private static String getFeelingTag(Report report) {
        String feelingTag = "#" + report.getTag1();
        if (report.getTag2() != null) {
            feelingTag += " #" + report.getTag2();
        } else if (report.getTag3() != null) {
            feelingTag += " #" + report.getTag3();
        }
        return feelingTag;
    }

}
