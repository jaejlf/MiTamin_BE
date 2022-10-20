package great.job.mytamin.domain.mytamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MentalConditionResponse {

    String dayOfWeek;
    int mentalConditionCode;

    public static MentalConditionResponse of(String dayOfWeek, int mentalConditionCode) {
        return MentalConditionResponse.builder()
                .dayOfWeek(dayOfWeek)
                .mentalConditionCode(mentalConditionCode)
                .build();
    }

}
