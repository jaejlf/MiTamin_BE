package great.job.mytamin.domain.mytamin.dto.response;

import great.job.mytamin.domain.mytamin.entity.Report;
import great.job.mytamin.domain.mytamin.enumerate.MentalCondition;
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
    Boolean canEdit;
    int mentalConditionCode;
    String mentalCondition;
    String feelingTag;
    String todayReport;

    public static ReportResponse of(Report report, String feelingTag, Boolean canEdit) {
        return ReportResponse.builder()
                .reportId(report.getReportId())
                .canEdit(canEdit)
                .mentalConditionCode(MentalCondition.convertMsgToCode(report.getMentalCondition()))
                .mentalCondition(report.getMentalCondition())
                .feelingTag(feelingTag)
                .todayReport(report.getTodayReport())
                .build();
    }

}
