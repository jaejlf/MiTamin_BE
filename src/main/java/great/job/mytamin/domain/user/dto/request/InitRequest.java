package great.job.mytamin.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class InitRequest {
    @NotNull boolean initReport;
    @NotNull boolean initCare;
    @NotNull boolean initMyday;
}
