package great.job.mytamin.domain.mytamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeelingRankResponse {

    String feeling;
    int count;

    public static FeelingRankResponse of(String feeling, int count) {
        return FeelingRankResponse.builder()
                .feeling(feeling)
                .count(count)
                .build();
    }

}
