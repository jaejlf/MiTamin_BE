package great.job.mytamin.domain.mytamin.dto.response;

import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.report.entity.Report;
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
    boolean canEdit;
    int memtalConditionCode;
    String feelingTag;
    String mentalConditionMsg;
    String todayReport;
    String careMsg1;
    String careMsg2;

    public static MytaminResponse of(Mytamin mytamin, int memtalConditionCode, boolean canEdit) {
        return MytaminResponse.builder()
                .takeAt(mytamin.getTakeAt())
                .canEdit(canEdit)
                .memtalConditionCode(memtalConditionCode)
                .feelingTag(getFeelingTag(mytamin.getReport()))
                .mentalConditionMsg("기분이 " + mytamin.getReport().getMentalCondition())
                .todayReport(mytamin.getReport().getTodayReport())
                .careMsg1(mytamin.getCare().getCareMsg1())
                .careMsg2(mytamin.getCare().getCareMsg2())
                .build();
    }

    public static MytaminResponse withReport(Mytamin mytamin, int memtalConditionCode, boolean canEdit) {
        return MytaminResponse.builder()
                .takeAt(mytamin.getTakeAt())
                .canEdit(canEdit)
                .memtalConditionCode(memtalConditionCode)
                .feelingTag(getFeelingTag(mytamin.getReport()))
                .mentalConditionMsg("기분이 " + mytamin.getReport().getMentalCondition())
                .todayReport(mytamin.getReport().getTodayReport())
                .build();
    }

    public static MytaminResponse withCare(Mytamin mytamin, boolean canEdit) {
        return MytaminResponse.builder()
                .takeAt(mytamin.getTakeAt())
                .canEdit(canEdit)
                .careMsg1(mytamin.getCare().getCareMsg1())
                .careMsg2(mytamin.getCare().getCareMsg2())
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