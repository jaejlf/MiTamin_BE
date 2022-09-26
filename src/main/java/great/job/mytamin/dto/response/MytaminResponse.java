package great.job.mytamin.dto.response;

import great.job.mytamin.domain.Mytamin;
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
    String feelingTag;
    String mentalConditionMsg;
    String todayReport;
    String careMsg1;
    String careMsg2;
    boolean canEdit;

    public static MytaminResponse of(Mytamin mytamin, boolean canEdit) {
        return MytaminResponse.builder()
                .takeAt(mytamin.getTakeAt())
                .feelingTag(mytamin.getReport().getFeelingTag())
                .mentalConditionMsg("기분이 " + mytamin.getReport().getMentalCondition())
                .todayReport(mytamin.getReport().getTodayReport())
                .careMsg1(mytamin.getCare().getCareMsg1())
                .careMsg2(mytamin.getCare().getCareMsg2())
                .canEdit(canEdit)
                .build();
    }

}
