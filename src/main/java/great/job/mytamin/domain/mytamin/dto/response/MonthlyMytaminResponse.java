package great.job.mytamin.domain.mytamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MonthlyMytaminResponse {
    int day;
    int mentalConditionCode;
}
