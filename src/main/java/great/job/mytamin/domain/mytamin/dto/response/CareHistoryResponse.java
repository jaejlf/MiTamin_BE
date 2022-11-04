package great.job.mytamin.domain.mytamin.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import great.job.mytamin.domain.mytamin.entity.Care;
import great.job.mytamin.domain.mytamin.enumerate.CareCategory;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareHistoryResponse {

    String careMsg1;
    String careMsg2;
    String careCategory;
    String takeAt;

    @JsonIgnore
    String title;

    public static CareHistoryResponse of(Care care) {
        LocalDateTime target = care.getTakeAt();
        String dayOfWeek = target.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        return CareHistoryResponse.builder()
                .careMsg1(care.getCareMsg1())
                .careMsg2(care.getCareMsg2())
                .careCategory("#" + CareCategory.convertCodeToMsg(care.getCareCategoryCode()))
                .takeAt(target.format(DateTimeFormatter.ofPattern("MM.dd")) + "." + dayOfWeek)
                .title(target.format(DateTimeFormatter.ofPattern("yyyy년 MM월")))
                .build();
    }

}
