package great.job.mytamin.topic.myday.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class DaynoteRequest {
    @NotBlank String wishText;
    @NotBlank String note;
    @NotBlank int year;
    @NotBlank int month;
}
