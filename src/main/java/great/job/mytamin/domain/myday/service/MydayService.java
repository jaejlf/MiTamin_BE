package great.job.mytamin.domain.myday.service;

import great.job.mytamin.domain.myday.dto.response.MydayResponse;
import great.job.mytamin.domain.myday.entity.Myday;
import great.job.mytamin.domain.myday.repository.MydayRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static great.job.mytamin.domain.myday.enumerate.MydayMessage.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class MydayService {

    private final TimeUtil timeUtil;
    private final MydayRepository mydayRepository;

    /*
    이번 달의 마이데이
    */
    @Transactional(readOnly = true)
    public MydayResponse getMyday(User user) {
        Myday myday = mydayRepository.findByMydayId(1L);
        LocalDateTime dateOfMyday = myday.getDateOfMyday();
        Map<String, String> map = getMydayInfo(user.getNickname(), dateOfMyday);
        return MydayResponse.of(
                dateOfMyday.format(DateTimeFormatter.ofPattern("MM월 dd일")),
                map.get("dday"),
                map.get("comment"));
    }

    @Transactional
    @Scheduled(cron = "0 0 0 1 * *") // 매월 1일 정각
    public void updateDateOfMyday() {

        log.info("### 마이데이 스케쥴러 실행 중 ... ###");

        // 15일 ~ 30일 중 랜덤 일자 생성
        int rangeStart = 15;
        int rangeEnd = 30;
        int randomizeDay = (int) (Math.random() * (rangeEnd - rangeStart + 1)) + rangeStart;

        // 마이데이 날짜 업데이트
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime dateOfMyday = LocalDateTime.of(
                now.getYear(),
                now.getMonth().getValue(),
                randomizeDay,
                0, 0);

        Myday myday = mydayRepository.findByMydayId(1L);
        myday.updateDateOfMyday(dateOfMyday);
        mydayRepository.save(myday);

        log.info(now.format(DateTimeFormatter.ofPattern("yy년 MM월의 마이데이 날짜 업데이트 완료")));

    }

    /*
    마이데이 디데이 & 메세지 계산
    */
    public Map<String, String> getMydayInfo(String nickname, LocalDateTime dateOfMyDay) {
        LocalDateTime now = LocalDateTime.now();
        int dday = now.getDayOfMonth() - dateOfMyDay.getDayOfMonth();

        Map<String, String> map = new HashMap<>();

        // 마이데이 당일
        if (dday == 0) {
            map.put("dday", "D-Day");
            map.put("comment", nickname + "님, " + THE_DAY_OF_MYDAY.getMsg());
        }
        // 마이데이 이전
        else if (dday < -3) {
            map.put("dday", "D" + dday + "일");
            map.put("comment", BEFORE_MYDAY.getMsg());
        }
        // 마이데이 3일 전
        else if (dday < 0) {
            map.put("dday", "D" + dday + "일");
            map.put("comment", SOON_MYDAY.getMsg());
        }
        // 마이데이 이후
        else {
            map.put("dday", "D+" + dday + "일");
            map.put("comment", AFTER_MYDAY.getMsg());
        }

        return map;
    }

}
