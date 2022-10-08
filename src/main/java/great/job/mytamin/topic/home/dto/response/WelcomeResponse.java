package great.job.mytamin.topic.home.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WelcomeResponse {

    String nickname;
    String comment;

    public static WelcomeResponse of(String nickname, String comment) {
        return WelcomeResponse.builder()
                .nickname(nickname)
                .comment(comment)
                .build();
    }

}
