package great.job.mytamin.domain.mytamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyMentalResponse {

    String dayOfWeek;
    int mentalConditionCode;

    public static WeeklyMentalResponse of(String dayOfWeek, int mentalConditionCode) {
        return WeeklyMentalResponse.builder()
                .dayOfWeek(dayOfWeek)
                .mentalConditionCode(mentalConditionCode)
                .build();
    }

}
