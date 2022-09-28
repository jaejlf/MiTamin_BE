package great.job.mytamin.global.service;

import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class TimeService {

    LocalDateTime now = LocalDateTime.now();

    /*
    LocalDateTime 포맷 변환 -> yyyy.MM.dd.요일
    */
    public String convertTimeFormat(LocalDateTime target) {
        String dayOfWeek = target.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        return target.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "." + dayOfWeek;
    }

    /*
    target이 now로부터 24시간 이내 생성된 데이터인지 (24시간 = 1440분)
    */
    public boolean isCreatedAtWithin24(LocalDateTime target) {
        Duration duration = Duration.between(target, now);
        return duration.toMinutes() < 1440;
    }

    /*
    하루 am 5:00 ~ am 4:59
    */
    public boolean isDay(LocalDateTime target) {
        LocalDateTime start1, start2, end1, end2;

        // start1 ~ end1 : am 5:00 ~ pm 11:59
        start1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 5, 0);
        end1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 23, 59);

        // start2 ~ end2 : am 12:00 ~ am : 4:59
        start2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 0, 0);
        end2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 4, 59);

        return isInRange(target, start1, end1) || isInRange(target, start2, end2);
    }

    /*
    아침 am 5:00 ~ am 11:59
    */
    public boolean isMorning() {
        LocalDateTime start, end;
        start = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 5, 0);
        end = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 11, 59);
        return isInRange(now, start, end);
    }

    /*
    오후 pm 12:00 ~ pm 18:59
    */
    public boolean isAfternoon() {
        LocalDateTime start, end;
        start = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 12, 0);
        end = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 18, 59);
        return isInRange(now, start, end);
    }

    /*
    저녁 pm 19:00 ~ am 4:59
    */
    public boolean isNight() {
        LocalDateTime start1, start2, end1, end2;
        start1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 19, 0);
        end1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 23, 59);
        start2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 0, 0);
        end2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 4, 59);
        return isInRange(now, start1, end1) || isInRange(now, start2, end2);
    }

    // 현재 시간이 특정 시간 범위 내에 있는지 확인
    private boolean isInRange(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return target.compareTo(start) >= 0 && end.compareTo(target) >= 0;
    }

}
