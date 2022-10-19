package great.job.mytamin.domain.mytamin.dto.response;

import great.job.mytamin.domain.mytamin.entity.Report;
import great.job.mytamin.domain.mytamin.enumerate.MentalCondition;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static great.job.mytamin.domain.mytamin.enumerate.MentalCondition.*;

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
        int mentalConditionCode = report.getMentalConditionCode();
        return ReportResponse.builder()
                .reportId(report.getReportId())
                .canEdit(canEdit)
                .mentalConditionCode(mentalConditionCode)
                .mentalCondition(convertCodeToMsg(mentalConditionCode))
                .feelingTag(feelingTag)
                .todayReport(report.getTodayReport())
                .build();
    }

}
