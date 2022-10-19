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
    Boolean isToday;

    public static WeeklyMentalResponse of(String dayOfWeek, int mentalConditionCode, Boolean isToday) {
        return WeeklyMentalResponse.builder()
                .dayOfWeek(dayOfWeek)
                .mentalConditionCode(mentalConditionCode)
                .isToday(isToday)
                .build();
    }

}
