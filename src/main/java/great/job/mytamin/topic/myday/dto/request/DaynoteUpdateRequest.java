package great.job.mytamin.topic.myday.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DaynoteUpdateRequest {
    @NotBlank String wishText;
    @NotBlank String note;
}
