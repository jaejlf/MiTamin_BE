package great.job.mytamin.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class TimeService {

    /*
    LocalDateTime 포맷 변환 -> yyyy.MM.dd.요일
    */
    public String convertTimeFormat(LocalDateTime target) {
        String dayOfWeek = target.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        return target.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "." + dayOfWeek;
    }

    /*
    24시간 이내 생성된 데이터인지 확인 (24시간 = 1440분)
    */
    public boolean isCreatedAtWithin24(LocalDateTime target) {
        LocalDateTime now = LocalDateTime.now();
        Duration duration = Duration.between(target, now);
        return duration.toMinutes() < 1440;
    }

    /*
    현재 시간이 특정 시간 범위 내에 있는지 확인
     */
    public boolean isInRange(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return target.compareTo(start) >= 0 && end.compareTo(target) >= 0;
    }

}
