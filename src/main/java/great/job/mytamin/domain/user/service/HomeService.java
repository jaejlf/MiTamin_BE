package great.job.mytamin.domain.user.service;

import great.job.mytamin.domain.user.dto.response.ActiveResponse;
import great.job.mytamin.domain.user.dto.response.WelcomeResponse;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.service.MytaminService;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static great.job.mytamin.domain.user.enumerate.WelcomeComment.*;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final TimeUtil timeUtil;
    private final MytaminService mytaminService;

    /*
    홈 화면 웰컴 메세지
    */
    @Transactional(readOnly = true)
    public WelcomeResponse welcome(User user) {
        Mytamin mytamin = mytaminService.findMytamin(user, LocalDateTime.now());
        String comment;

        if (mytamin != null) comment = TAKE_MYTAMIN_DONE.getComment();
        else {
            if (user.getAlarm().getMytaminAlarmOn()) {
                int mytaminHour = Integer.parseInt(user.getAlarm().getMytaminHour());
                int mytaminMin = Integer.parseInt(user.getAlarm().getMytaminMin());
                comment = getCommentByMytaminTime(mytaminHour, mytaminMin);
            } else {
                comment = getCommentByCurrentTime();
            }
        }
        return WelcomeResponse.of(user.getNickname(), comment);
    }

    /*
    행동에 따른 버튼 활성화 여부
    */
    @Transactional(readOnly = true)
    public ActiveResponse getProgressStatus(User user) {
        Boolean breathIsDone = timeUtil.isToday(user.getAction().getBreathTime());
        Boolean senseIsDone = timeUtil.isToday(user.getAction().getSenseTime());

        Mytamin mytamin = mytaminService.findMytamin(user, LocalDateTime.now());
        Boolean reportIsDone = mytamin != null && mytamin.getReport() != null;
        Boolean careIsDone = mytamin != null && mytamin.getCare() != null;

        return ActiveResponse.of(breathIsDone, senseIsDone, reportIsDone, careIsDone);
    }

    // 마이타민 섭취 지정 시간 (+-2시간)에 따른 메세지 반환
    /*
    지정 2시간 전 : 오늘의 마이타민 섭취를 잊지마세요 :)
    지정 2시간 후 : 마이타민 섭취.. 잊으시면 안 돼요 !
    */
    private String getCommentByMytaminTime(int mytaminHour, int mytaminMin) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime mytaminTime = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), mytaminHour, mytaminMin);
        LocalDateTime before2H = timeUtil.isToday(mytaminTime.minusHours(2)) ? mytaminTime.minusHours(2) : LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 5, 0);
        LocalDateTime after2H = timeUtil.isToday(mytaminTime.plusHours(2)) ? mytaminTime.plusHours(2) : LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 4, 59);

        if (timeUtil.isInRange(now, before2H, mytaminTime)) return BEFORE_MYTAMIN_TIME.getComment();
        else if (timeUtil.isInRange(now, mytaminTime, after2H)) return AFTER_MYTAMIN_TIME.getComment();
        else return getCommentByCurrentTime();
    }

    // 시간에 따른 메세지 반환
    /*
    am 5:00 ~ am 11:59 : 오늘도 힘차게 시작해볼까요 ?
    pm 12:00 ~ pm 18:59 : 어떤 하루를 보내고 계신가요 ?
    pm 19:00 ~ am 4:59 : 푹 쉬고 내일 만나요
    */
    private String getCommentByCurrentTime() {
        LocalDateTime now = LocalDateTime.now();
        if (timeUtil.isMorning(now)) return MORNING.getComment();
        if (timeUtil.isAfternoon(now)) return AFTERNOON.getComment();
        if (timeUtil.isNight(now)) return NIGHT.getComment();

        return "*** 이 메세지가 뜬다면 시간 설정 오류 ***";
    }

}