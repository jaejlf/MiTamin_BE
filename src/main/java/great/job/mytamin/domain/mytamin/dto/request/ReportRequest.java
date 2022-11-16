package great.job.mytamin.domain.mytamin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class ReportRequest {
    @NotNull int mentalConditionCode;
    @NotBlank String tag1;
    String tag2;
    String tag3;
    @NotBlank String todayReport;
}
