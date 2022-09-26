package great.job.mytamin.dto.response;

import great.job.mytamin.domain.Care;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareResponse {

    String takeAt;
    String careCategory;
    String careMsg1;
    String careMsg2;

    public static CareResponse of(Care care) {
        return CareResponse.builder()
                .takeAt(care.getMytamin().getTakeAt())
                .careCategory(care.getCareCategory())
                .careMsg1(care.getCareMsg1())
                .careMsg2(care.getCareMsg2())
                .build();
    }

}
