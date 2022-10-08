package great.job.mytamin.topic.mytamin.dto.response;

import great.job.mytamin.topic.mytamin.entity.Report;
import great.job.mytamin.topic.mytamin.enumerate.MentalCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ReportResponse {

    Long reportId;
    int mentalConditionCode;
    String mentalCondition;
    String feelingTag;
    String todayReport;

    public static ReportResponse of(Report report, String feelingTag) {
        return ReportResponse.builder()
                .reportId(report.getReportId())
                .mentalConditionCode(MentalCondition.convertMsgToCode(report.getMentalCondition()))
                .mentalCondition(report.getMentalCondition())
                .feelingTag(feelingTag)
                .todayReport(report.getTodayReport())
                .build();
    }

}
