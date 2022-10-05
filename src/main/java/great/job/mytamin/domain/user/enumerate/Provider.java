package great.job.mytamin.domain.user.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Provider {

    DEFAULT("일반 회원"),
    KAKAO("카카오톡 회원");

    private final String provider;

}
