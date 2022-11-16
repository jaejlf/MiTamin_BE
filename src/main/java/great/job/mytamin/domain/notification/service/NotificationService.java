package great.job.mytamin.domain.notification.service;

import great.job.mytamin.domain.myday.entity.Myday;
import great.job.mytamin.domain.myday.repository.MydayRepository;
import great.job.mytamin.domain.user.entity.Alarm;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.repository.AlarmRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import static great.job.mytamin.domain.myday.enumerate.MydayAlarm.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final FcmService fcmService;
    private final AlarmRepository alarmRepository;
    private final MydayRepository mydayRepository;

    @Transactional
    @Scheduled(cron = "0 */5 * * * *") // 5분 간격
    public void notificationScheduler() throws IOException {
        log.info("### 푸시알림 스케쥴러 실행 중 ... ###");
        LocalDateTime now = LocalDateTime.now();
        notifyMytamin(now);
        notifyMyday(now);
    }

    public void notifyMytamin(LocalDateTime now) throws IOException {
        List<Alarm> mytaminAlarmList = alarmRepository.findByMytaminAlarmOnAndMytaminHourAndMytaminMin(
                true,
                now.format(DateTimeFormatter.ofPattern("HH")),
                now.format((DateTimeFormatter.ofPattern("mm")))
        );

        if (!mytaminAlarmList.isEmpty()) {
            sendMessage(
                    mytaminAlarmList,
                    "마이타민 섭취 시간",
                    "님! 오늘 하루는 어떠셨나요? 오늘의 마이타민을 섭취하시면서 하루를 마무리해보세요 💊");

            log.info(">>> 마이타민 섭취 시간 알림 발송 완료 <<<");
        }
    }

    public void notifyMyday(LocalDateTime now) throws IOException {
        Myday myday = mydayRepository.findByMydayId(1L);
        LocalDateTime target = LocalDateTime.of(now.getYear(), now.getMonth(), now.getDayOfMonth(), 10, 0);

        List<Alarm> mydayAlarmList = new ArrayList<>();
        String when = "";

        if (target.equals(myday.getToday())) {
            when = TODAY.getMsg();
            mydayAlarmList = alarmRepository.findByMydayAlarmOnAndMydayWhen(true, when);
        } else if (target.equals(myday.getDayAgo())) {
            when = DAY_AGO.getMsg();
            mydayAlarmList = alarmRepository.findByMydayAlarmOnAndMydayWhen(true, when);
        } else if (target.equals(myday.getWeekAgo())) {
            when = WEEK_AGO.getMsg();
            mydayAlarmList = alarmRepository.findByMydayAlarmOnAndMydayWhen(true, when);
        }

        if (!mydayAlarmList.isEmpty()) {
            sendMessage(
                    mydayAlarmList,
                    "마이데이 " + when,
                    "님! 마이데이가 곧## 💊");

            log.info(">>> 마이데이 섭취 시간 알림 발송 완료 <<<");
        }
    }

    private void sendMessage(List<Alarm> alarmList, String title, String body) throws IOException {
        for (Alarm alarm : alarmList) {
            User user = alarm.getUser();
            fcmService.sendMessageTo(user.getFcmToken(), title, user.getNickname() + body);
        }
    }

}