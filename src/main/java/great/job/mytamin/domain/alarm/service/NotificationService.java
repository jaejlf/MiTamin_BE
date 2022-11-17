package great.job.mytamin.domain.alarm.service;

import com.google.firebase.messaging.FirebaseMessagingException;
import great.job.mytamin.domain.alarm.entity.FcmOn;
import great.job.mytamin.domain.alarm.enumerate.MydayAlarm;
import great.job.mytamin.domain.alarm.repository.FcmOnRepository;
import great.job.mytamin.domain.myday.entity.Myday;
import great.job.mytamin.domain.myday.repository.MydayRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static great.job.mytamin.domain.alarm.enumerate.MydayAlarm.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FcmService fcmService;
    private final FcmOnRepository fcmOnRepository;
    private final MydayRepository mydayRepository;

    @Transactional
    @Scheduled(cron = "0 */5 * * * *") // 5분 간격
    public void notificationScheduler() throws FirebaseMessagingException {
        log.info("### 푸시알림 스케쥴러 실행 중 ... ###");
        LocalDateTime now = LocalDateTime.now();
        notifyMytamin(now);
        notifyMyday(now);
    }

    public void notifyMytamin(LocalDateTime now) throws FirebaseMessagingException {
        String mytaminWhen = now.format(DateTimeFormatter.ofPattern("HH : mm"));
        List<FcmOn> fcmOnList = fcmOnRepository.findAllByMytaminWhen(mytaminWhen);
        if (!fcmOnList.isEmpty()) {
            for (FcmOn target : fcmOnList) {
                fcmService.sendTargetMessage(
                        target.getFcmToken(),
                        "마이타민 섭취 시간",
                        target.getUser().getNickname() + "님! 오늘 하루는 어떠셨나요? 오늘의 마이타민을 섭취하시면서 하루를 마무리해보세요💊");
            }
            log.info(">>> 마이타민 섭취 시간 알림 발송 완료 <<<");
        }
    }

    public void notifyMyday(LocalDateTime now) throws FirebaseMessagingException {
        Myday myday = mydayRepository.findByMydayId(1L);
        LocalDateTime target = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 10, 0);

        MydayAlarm mydayAlarm = NONE;
        if (target.equals(myday.getToday())) mydayAlarm = TODAY;
        else if (target.equals(myday.getDayAgo())) mydayAlarm = DAY_AGO;
        else if (target.equals(myday.getWeekAgo())) mydayAlarm = WEEK_AGO;

        if (mydayAlarm != NONE) {
            fcmService.sendTopicMessage(mydayAlarm.getTopic(), "두근두근 마이데이", mydayAlarm.getBody());
            log.info(">>> 마이데이 섭취 시간 알림 발송 완료 <<<");
        }
    }

}