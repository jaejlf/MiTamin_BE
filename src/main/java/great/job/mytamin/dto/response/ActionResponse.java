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

    private String message;
    private String updatedTime;

    public static ActionResponse of(String updatedTime) {
        return ActionResponse.builder()
                .message("최종 업데이트 시간")
                .updatedTime(updatedTime)
                .build();
    }

}
