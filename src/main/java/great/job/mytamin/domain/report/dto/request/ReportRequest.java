package great.job.mytamin.domain.report.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    @NotBlank int mentalConditionCode;
    @NotBlank String tag1;
    String tag2;
    String tag3;
    @NotBlank String todayReport;
}
