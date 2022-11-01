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
public class ProfileResponse {

    String nickname;
    String profileImgUrl;
    String beMyMessage;
    String provider;

    public static ProfileResponse of(User user) {
        return ProfileResponse.builder()
                .nickname(user.getNickname())
                .profileImgUrl(user.getProfileImgUrl())
                .beMyMessage(user.getBeMyMessage())
                .provider(user.getProvider().getInfo())
                .build();
    }

}
