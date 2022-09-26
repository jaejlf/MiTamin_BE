package great.job.mytamin.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ActionResponse {

    private String updatedTime;

    public static ActionResponse of(String updatedTime) {
        return ActionResponse.builder()
                .updatedTime(updatedTime)
                .build();
    }

}
