package great.job.mytamin.domain.home.service;

import great.job.mytamin.domain.home.dto.response.ActionResponse;
import great.job.mytamin.domain.home.dto.response.ActiveResponse;
import great.job.mytamin.domain.home.dto.response.WelcomeResponse;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.service.MytaminService;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.service.TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static great.job.mytamin.domain.mytamin.enumerate.WelcomeComment.*;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final TimeService timeService;
    private final MytaminService mytaminService;

    LocalDateTime now = LocalDateTime.now();

    /*
    홈 화면 웰컴 메세지
    */
    @Transactional(readOnly = true)
    public WelcomeResponse welcome(User user) {
        String comment = "*** 이 메세지가 뜬다면 시간 설정 오류 **";
        String takeAt = timeService.convertToTakeAt(now);
        Mytamin mytamin = mytaminService.getMytamin(user, takeAt);

        // 오늘의 마이타민 섭취 완료
        if (mytamin != null) {
            comment = TAKE_MYTAMIN_DONE.getComment();
        }

        // 오늘의 마이타민 섭취 이전
        else {
            // 마이타민 섭취 지정 시간이 존재할 경우
            if (user.getMytaminHour() != null) {
                int mytaminHour = Integer.parseInt(user.getMytaminHour());
                int mytaminMin = Integer.parseInt(user.getMytaminMin());
                comment = getCommentByMytaminTime(mytaminHour, mytaminMin);
            }
            // 마이타민 섭취 지정 시간이 존재하지 않을 경우
            else {
                comment = getCommentByCurrentTime();
            }
        }

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
        boolean breathIsDone = timeService.isToday(user.getBreathTime());
        boolean senseIsDone = timeService.isToday(user.getSenseTime());

        boolean reportIsDone, careIsDone;
        String takeAt = timeService.convertToTakeAt(now);
        Mytamin mytamin = mytaminService.getMytamin(user, takeAt);
        if (mytamin == null) {
            reportIsDone = false;
            careIsDone = false;
        } else {
            reportIsDone = mytamin.getReport() != null;
            careIsDone = mytamin.getCare() != null;
        }
        return ActiveResponse.of(breathIsDone, senseIsDone, reportIsDone, careIsDone);
    }

    // 마이타민 섭취 지정 시간 (+-2시간)에 따른 메세지 반환
    /*
    지정 2시간 전 : 오늘의 마이타민 섭취를 잊지마세요 :)
    지정 2시간 후 : 마이타민 섭취.. 잊으시면 안 돼요 !
    */
    private String getCommentByMytaminTime(int mytaminHour, int mytaminMin) {
        LocalDateTime mytaminTime = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), mytaminHour, mytaminMin);

        LocalDateTime beforeMyTime = timeService.isToday(mytaminTime.minusHours(2)) ? mytaminTime.minusHours(2) : LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 5, 0);
        LocalDateTime afterMyTime = timeService.isToday(mytaminTime.plusHours(2)) ? mytaminTime.plusHours(2) : LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 4, 59);

        if (timeService.isInRange(now, beforeMyTime, mytaminTime)) return BEFORE_MYTAMIN_TIME.getComment();
        else if (timeService.isInRange(now, mytaminTime, afterMyTime)) return AFTER_MYTAMIN_TIME.getComment();
        else return getCommentByCurrentTime();
    }

    // 시간에 따른 메세지 반환
    /*
    am 5:00 ~ am 11:59 : 오늘도 힘차게 시작해볼까요 ?
    pm 12:00 ~ pm 18:59 : 어떤 하루를 보내고 계신가요 ?
    pm 19:00 ~ am 4:59 : 푹 쉬고 내일 만나요
    */
    private String getCommentByCurrentTime() {
        if (timeService.isMorning(now)) return MORNING.getComment();
        if (timeService.isAfternoon(now)) return AFTERNOON.getComment();
        if (timeService.isNight(now)) return NIGHT.getComment();

        return "*** 이 메세지가 뜬다면 시간 설정 오류 ***";
    }

}