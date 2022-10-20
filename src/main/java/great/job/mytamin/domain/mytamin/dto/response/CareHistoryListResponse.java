package great.job.mytamin.domain.mytamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CareHistoryListResponse {

    Map<String, List<CareHistoryResponse>> careHistory;

    public static CareHistoryListResponse of(Map<String, List<CareHistoryResponse>> careHistory) {
        return CareHistoryListResponse.builder()
                .careHistory(careHistory)
                .build();
    }

}
