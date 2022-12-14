package great.job.mytamin.domain.user.dto.response;

import great.job.mytamin.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    String email;
    String nickname;
    String profileImgUrl;
    String beMyMessage;
    String mytaminHour;
    String mytaminMin;

    public static UserResponse of(User user) {
        return UserResponse.builder()
                .email(user.getEmail())
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .beMyMessage(user.getBeMyMessage())
                .mytaminHour(user.getAlarm().getMytaminHour())
                .mytaminMin(user.getAlarm().getMytaminMin())
                .build();
    }

}
