package great.job.mytamin.topic.myday.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class WishRequest {
    Long wishId;
    @NotBlank String text;
    @NotBlank Boolean isHidden;
    @NotBlank int orderId;
}
