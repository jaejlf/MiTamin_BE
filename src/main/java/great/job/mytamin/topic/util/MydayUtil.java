package great.job.mytamin.topic.util;

import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Component
public class MydayUtil {

    /*
    마이데이 날짜 랜덤 지정
    */
    @Transactional(readOnly = true)
    public LocalDateTime randomizeDateOfMyday() {
        LocalDateTime now = LocalDateTime.now();

        // 15일 ~ 30일
        int rangeStart = 15;
        int rangeEnd = 30;
        int randomizeDay = (int) (Math.random() * (rangeEnd - rangeStart + 1)) + rangeStart;

        // 랜덤 날짜 리턴
        return LocalDateTime.of(
                now.getYear(),
                now.getMonth().getValue(),
                randomizeDay,
                0, 0);
    }

}
