package great.job.mytamin.domain.report.dto.response;

import great.job.mytamin.domain.report.entity.Report;
import great.job.mytamin.domain.report.enumerate.MentalCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    int mentalConditionCode;
    String mentalCondition;
    String feelingTag;
    String todayReport;

    public static ReportResponse of(Report report, String feelingTag) {
        return ReportResponse.builder()
                .mentalConditionCode(MentalCondition.convertMsgToCode(report.getMentalCondition()))
                .mentalCondition(report.getMentalCondition())
                .feelingTag(feelingTag)
                .todayReport(report.getTodayReport())
                .build();
    }

}
