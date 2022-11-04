package great.job.mytamin.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InitRequest {
    @NotBlank boolean initReport;
    @NotBlank boolean initCare;
    @NotBlank boolean initMyday;
}
