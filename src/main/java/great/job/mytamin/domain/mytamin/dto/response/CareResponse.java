package great.job.mytamin.domain.mytamin.dto.response;

import great.job.mytamin.domain.mytamin.entity.Care;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static great.job.mytamin.domain.mytamin.enumerate.CareCategory.convertCodeToMsg;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareResponse {

    Long careId;
    Boolean canEdit;
    String careCategory;
    String careMsg1;
    String careMsg2;

    public static CareResponse of(Care care, Boolean canEdit) {
        return CareResponse.builder()
                .careId(care.getCareId())
                .canEdit(canEdit)
                .careCategory(convertCodeToMsg(care.getCareCategoryCode()))
                .careMsg1(care.getCareMsg1())
                .careMsg2(care.getCareMsg2())
                .build();
    }

}
