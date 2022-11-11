package great.job.mytamin.domain.mytamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyMentalConditionResponse {

    String dayOfWeek;
    int mentalConditionCode;

    public static WeeklyMentalConditionResponse of(String dayOfWeek, int mentalConditionCode) {
        return WeeklyMentalConditionResponse.builder()
                .dayOfWeek(dayOfWeek)
                .mentalConditionCode(mentalConditionCode)
                .build();
    }

}
