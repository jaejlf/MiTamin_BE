package great.job.mytamin.domain.myday.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WishRequest {
    Long wishId;
    @NotBlank String wishText;
    @NotBlank Boolean isHidden;
    @NotBlank int orderId;
}
