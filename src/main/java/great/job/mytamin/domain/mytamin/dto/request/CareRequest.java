package great.job.mytamin.domain.mytamin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CareRequest {
    @NotBlank int careCategoryCode;
    @NotBlank String careMsg1;
    @NotBlank String careMsg2;
}
