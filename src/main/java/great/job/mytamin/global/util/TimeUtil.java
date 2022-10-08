package great.job.mytamin.global.util;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static great.job.mytamin.topic.user.enumerate.MydayMessage.*;

@Component
public class TimeUtil {

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

    /*
    target이 이번 달의 날짜에 속하는지
    */
    public boolean isCurrentMonth(LocalDateTime target) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(now.getYear(), now.getMonth().getValue() + 1, 1, 23, 59).minusDays(1);
        return isInRange(target, start, end);
    }

    /*
    마이데이 디데이 & 메세지 계산
    */
    public Map<String, String> getMyDayInfo(String nickname, LocalDateTime dateOfMyDay) {
        LocalDateTime now = LocalDateTime.now();
        int dday = now.getDayOfMonth() - dateOfMyDay.getDayOfMonth();

        Map<String, String> map = new HashMap<>();

        // 마이데이 당일
        if (dday == 0) {
            map.put("dday", "D-Day");
            map.put("msg", nickname + "님, " + THE_DAY_OF_MYDAY.getMsg());
        }
        // 마이데이 이전
        else if (dday < -3) {
            map.put("dday", "D" + dday + "일");
            map.put("msg", BEFORE_MYDAY.getMsg());
        }
        // 마이데이 3일 전
        else if (dday < 0) {
            map.put("dday", "D" + dday + "일");
            map.put("msg", SOON_MYDAY.getMsg());
        }
        // 마이데이 이후
        else {
            map.put("dday", "D+" + dday + "일");
            map.put("msg", AFTER_MYDAY.getMsg());
        }

        return map;
    }

    // 특정 시간 범위 내에 있는지
    public boolean isInRange(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return target.compareTo(start) >= 0 && end.compareTo(target) >= 0;
    }

}
