package great.job.mytamin.topic.user.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SettingInfoResponse {

    Boolean isOn;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String when;

    public static SettingInfoResponse of(Boolean isOn, String when) {
        return SettingInfoResponse.builder()
                .isOn(isOn)
                .when(when)
                .build();
    }

}
