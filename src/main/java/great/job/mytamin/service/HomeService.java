package great.job.mytamin.service;

import great.job.mytamin.domain.Mytamin;
import great.job.mytamin.domain.User;
import great.job.mytamin.dto.response.ActionResponse;
import great.job.mytamin.dto.response.ActiveResponse;
import great.job.mytamin.dto.response.WelcomeResponse;
import great.job.mytamin.repository.MytaminRepository;
import great.job.mytamin.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final TimeService timeService;
    private final UserRepository userRepository;
    private final MytaminRepository mytaminRepository;

    LocalDateTime start1, end1, start2, end2;
    LocalDateTime now = LocalDateTime.now();

    /*
    홈 화면 웰컴 메세지
    */
    @Transactional(readOnly = true)
    public WelcomeResponse welcome(User user) {
        /*
        마이타민 섭취 여부에 따른 -> 구현 전
        */
        // 마이타민 섭취 지정 시간이 없는 경우
        // 마이타민 섭취 지정 시간이 있는 경우
        // ~ 지정 시간 이전 : 오늘의 마이타민 섭취를 잊지마세요 :)
        // 섭취 완료 : 오늘 하루도 수고 많았어요 :)
        // 섭취 X : 마이타민 섭취.. 잊으시면 안 돼요 !

        /*
        시간에 따른 -> 구현 완료
        */
        String comment = getCommentOverTime();
        return WelcomeResponse.of(user.getNickname(), comment);
    }

    /*
    숨 고르기 완료
    */
    @Transactional
    public ActionResponse completeBreath(User user) {
        user.updateBreathTime();
        String updatedTime = user.getBreathTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        return ActionResponse.of(updatedTime);
    }

    /*
    감각 깨우기 완료
    */
    @Transactional
    public ActionResponse completeSense(User user) {
        user.updateSenseTime();
        String updatedTime = user.getSenseTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        return ActionResponse.of(updatedTime);
    }

    /*
    행동에 따른 버튼 활성화 여부
    */
    @Transactional(readOnly = true)
    public ActiveResponse getProgressStatus(User user) {
        // 하루 기준 : am 5:00 ~ am 4:59
        start1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 5, 0);
        end1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 11, 59);
        start2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 0, 0);
        end2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 4, 59);

        boolean breathIsDone = timeService.isInRange(user.getBreathTime(), start1, end1) || timeService.isInRange(user.getBreathTime(), start2, end2);
        boolean senseIsDone = timeService.isInRange(user.getSenseTime(), start1, end1) || timeService.isInRange(user.getSenseTime(), start2, end2);

        boolean reportIsDone, careIsDone;
        String takeAt = timeService.convertTimeFormat(LocalDateTime.now());
        Mytamin mytamin = mytaminRepository.findByTakeAtAndUser(takeAt, user);
        if (mytamin == null) {
            reportIsDone = false;
            careIsDone = false;
        } else {
            reportIsDone = mytamin.getReport() != null;
            careIsDone = mytamin.getCare() != null;
        }
        return ActiveResponse.of(breathIsDone, senseIsDone, reportIsDone, careIsDone);
    }

    // 시간에 따른 메세지 반환
    private String getCommentOverTime() {
        // am 5:00 ~ am 11:59 : 오늘도 힘차게 시작해볼까요 ?
        start1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 5, 0);
        end1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 11, 59);
        if (timeService.isInRange(now, start1, end1)) return "오늘도 힘차게 시작해볼까요 ?";

        // pm 12:00 ~ pm 18:59 : 어떤 하루를 보내고 계신가요 ?
        start1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 12, 0);
        end1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 18, 59);
        if (timeService.isInRange(now, start1, end1)) return "어떤 하루를 보내고 계신가요 ?";

        // pm 19:00 ~ am 4:59 : 푹 쉬고 내일 만나요
        start1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 19, 0);
        end1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 11, 59);
        start2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 0, 0);
        end2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 4, 59);
        if (timeService.isInRange(now, start1, end1) || timeService.isInRange(now, start2, end2)) return "푹 쉬고 내일 만나요";

        return "*** 이 메세지가 뜬다면 시간 설정 오류이니 서버에게 알려주세요 ***";
    }

}