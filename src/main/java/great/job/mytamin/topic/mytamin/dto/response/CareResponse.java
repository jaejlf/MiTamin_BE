package great.job.mytamin.topic.mytamin.dto.response;

import great.job.mytamin.topic.mytamin.entity.Care;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareResponse {

    Long careId;
    boolean canEdit;
    String careCategory;
    String careMsg1;
    String careMsg2;

    public static CareResponse of(Care care, boolean canEdit) {
        return CareResponse.builder()
                .careId(care.getCareId())
                .canEdit(canEdit)
                .careCategory(care.getCareCategory())
                .careMsg1(care.getCareMsg1())
                .careMsg2(care.getCareMsg2())
                .build();
    }

}
