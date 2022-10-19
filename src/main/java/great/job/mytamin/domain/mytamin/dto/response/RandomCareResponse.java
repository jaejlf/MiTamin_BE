package great.job.mytamin.domain.mytamin.dto.response;

import great.job.mytamin.domain.mytamin.entity.Care;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RandomCareResponse {

    String careMsg1;
    String careMsg2;
    String takeAt;

    public static RandomCareResponse of(Care care) {
        return RandomCareResponse.builder()
                .careMsg1(care.getCareMsg1())
                .careMsg2(care.getCareMsg2())
                .takeAt(care.getMytamin().getTakeAt())
                .build();
    }

}
