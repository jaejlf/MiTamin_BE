package great.job.mytamin.domain.mytamin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CareRequest {
    @NotNull int careCategoryCode;
    @NotBlank String careMsg1;
    @NotBlank String careMsg2;
}
