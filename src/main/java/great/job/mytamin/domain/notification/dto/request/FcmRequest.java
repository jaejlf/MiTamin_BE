package great.job.mytamin.domain.notification.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FcmRequest {
    @NotBlank String targetToken;
    @NotBlank String title;
    @NotBlank String body;
}
