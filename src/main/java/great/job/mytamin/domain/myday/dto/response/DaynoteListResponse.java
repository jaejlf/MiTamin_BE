package great.job.mytamin.domain.myday.dto.response;

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
public class DaynoteListResponse {

    Map<Integer, List<DaynoteResponse>> daynoteList;

    public static DaynoteListResponse of(Map<Integer, List<DaynoteResponse>> daynoteListMap) {
        return DaynoteListResponse.builder()
                .daynoteList(daynoteListMap)
                .build();
    }

}
