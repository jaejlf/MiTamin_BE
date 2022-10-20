package great.job.mytamin.domain.mytamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeeklyMentalReportResponse {

    String dayOfWeek;
    int mentalConditionCode;

    public static WeeklyMentalReportResponse of(String dayOfWeek, int mentalConditionCode) {
        return WeeklyMentalReportResponse.builder()
                .dayOfWeek(dayOfWeek)
                .mentalConditionCode(mentalConditionCode)
                .build();
    }

}
