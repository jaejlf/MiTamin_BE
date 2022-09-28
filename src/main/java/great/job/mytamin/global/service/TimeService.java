package great.job.mytamin.global.service;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@Service
public class TimeService {

    LocalDateTime start, end;

    /*
    LocalDateTime -> takeAt 포맷 변환 (yyyy.MM.dd.요일)
    */
    public String convertToTakeAt(LocalDateTime target) {
        if (target.getHour() <= 4) target = target.minusDays(1);
        String dayOfWeek = target.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        return target.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "." + dayOfWeek;
    }

    /*
    target이 오늘(am 5:00 ~ 4:59)의 범위에 속하는지
    */
    public boolean isToday(LocalDateTime target) {
        LocalDateTime start1, start2, end1, end2;
        LocalDateTime now = LocalDateTime.now();

        if (now.getHour() <= 4) {
            start1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth() - 1, 5, 0);
            end1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth() - 1, 23, 59);
            start2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 0, 0);
            end2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 4, 59);
        } else {
            start1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 5, 0);
            end1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 23, 59);
            start2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth() + 1, 0, 0);
            end2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth() + 1, 4, 59);
        }

        return isInRange(target, start1, end1) || isInRange(target, start2, end2);
    }

    /*
    아침 am 5:00 ~ am 11:59
    */
    public boolean isMorning(LocalDateTime target) {
        return target.getHour() >= 5 && target.getHour() <= 11;
    }

    /*
    오후 pm 12:00 ~ pm 18:59
    */
    public boolean isAfternoon(LocalDateTime target) {
        return target.getHour() >= 12 && target.getHour() <= 18;
    }

    /*
    밤 pm 19:00 ~ am 4:59
    */
    public boolean isNight(LocalDateTime target) {
        return target.getHour() >= 19 || target.getHour() <= 4;
    }

    // 특정 시간 범위 내에 있는지
    public boolean isInRange(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return target.compareTo(start) >= 0 && end.compareTo(target) >= 0;
    }

}
