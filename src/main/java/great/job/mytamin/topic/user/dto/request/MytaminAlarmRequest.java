package great.job.mytamin.topic.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MytaminAlarmRequest {
    @NotBlank String mytaminHour;
    @NotBlank String mytaminMin;
}
