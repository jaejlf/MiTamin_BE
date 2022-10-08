package great.job.mytamin.topic.home.enumerate;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum WelcomeComment {

    MORNING("오늘도 힘차게 시작해볼까요 ?"),
    AFTERNOON("어떤 하루를 보내고 계신가요 ?"),
    NIGHT("푹 쉬고 내일 만나요"),
    TAKE_MYTAMIN_DONE("오늘 하루도 수고 많았어요 :)"),
    BEFORE_MYTAMIN_TIME("오늘의 마이타민 섭취를 잊지마세요 :)"),
    AFTER_MYTAMIN_TIME("마이타민 섭취.. 잊으시면 안 돼요 !");

    private final String comment;

}
