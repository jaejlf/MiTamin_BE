package great.job.mytamin.topic.user.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingResponse {

    SettingInfoResponse mytamin;
    SettingInfoResponse myday;

    public static SettingResponse of(SettingInfoResponse mytamin, SettingInfoResponse myday) {
        return SettingResponse.builder()
                .mytamin(mytamin)
                .myday(myday)
                .build();
    }

}

