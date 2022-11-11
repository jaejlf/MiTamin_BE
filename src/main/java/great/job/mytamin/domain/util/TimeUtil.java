package great.job.mytamin.domain.util;

import great.job.mytamin.domain.mytamin.entity.Care;
import great.job.mytamin.domain.mytamin.entity.Report;
import great.job.mytamin.global.exception.MytaminException;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import static great.job.mytamin.domain.user.enumerate.MydayMessage.*;
import static great.job.mytamin.global.exception.ErrorMap.DATETIME_PARSE_ERROR;

@Component
public class TimeUtil {

    /*
    마이타민 섭취 시간 포맷터
    LocalDateTime -> MM.dd.요일
    */
    public LocalDateTime convertToMytaminDate(LocalDateTime target) {
        if (target.getHour() <= 4) target = target.minusDays(1);
        return LocalDateTime.of(target.getYear(), target.getMonth().getValue(), target.getDayOfMonth(), 10, 0);
    }

    /*
    LocalDateTime -> 시간(10:00) 커스텀 세팅
    */
    public LocalDateTime convertToCustomHHmm(LocalDateTime target) {
        return LocalDateTime.of(target.getYear(), target.getMonth().getValue(), target.getDayOfMonth(), 10, 0);
    }

    /*
    yyyy.MM -> LocalDateTime
    */
    public LocalDateTime convertRawToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm");
        LocalDateTime rawPerformedAt = LocalDateTime.parse(date + ".10.10.00", formatter); // 10일.10시.00분으로 임의 설정

        // 2020년 이전 or 올해 년도 이후
        if (rawPerformedAt.getYear() < 2020 || rawPerformedAt.getYear() > LocalDateTime.now().getYear()) {
            throw new MytaminException(DATETIME_PARSE_ERROR);
        }
        return rawPerformedAt;
    }

    /*
    yyyy.MM.dd -> LocalDateTime
    */
    public LocalDateTime convertRawDDToLocalDateTime(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd.HH.mm");
        LocalDateTime rawPerformedAt = LocalDateTime.parse(date + ".10.00", formatter); // 10시.00분으로 임의 설정

        // 2020년 이전 or 올해 년도 이후
        if (rawPerformedAt.getYear() < 2020 || rawPerformedAt.getYear() > LocalDateTime.now().getYear()) {
            throw new MytaminException(DATETIME_PARSE_ERROR);
        }
        return rawPerformedAt;
    }

    /*
    알림 설정 포맷터
    String HH, MM -> 오전/오후 HH:mm
    */
    public String convertToAlarmFormat(String HH, String mm) {
        int hour = Integer.parseInt(HH);
        int minute = Integer.parseInt(mm);

        String apm = "오전";
        if (hour > 12) {
            hour -= 12;
            apm = "오후";
        }

        LocalDateTime alarmAt = LocalDateTime.of(2022, 10, 10, hour, minute);
        return alarmAt.format(DateTimeFormatter.ofPattern(apm + " HH:mm"));
    }

    /*
    요일 숫자 -> 한글
    */
    public String convertDayNumToStr(int dayOfWeek) {
        if (dayOfWeek == 1) return "월";
        else if (dayOfWeek == 2) return "화";
        else if (dayOfWeek == 3) return "수";
        else if (dayOfWeek == 4) return "목";
        else if (dayOfWeek == 5) return "금";
        else if (dayOfWeek == 6) return "토";
        else if (dayOfWeek == 7) return "일";
        else throw new MytaminException(DATETIME_PARSE_ERROR);
    }

    /*
    target이 오늘(am 5:00 ~ 4:59)의 범위에 속하는지
    */
    public Boolean isToday(LocalDateTime target) {
        LocalDateTime start1, start2, end1, end2;
        LocalDateTime now = LocalDateTime.now();

        if (now.getHour() <= 4) {
            start1 = now.minusDays(1).withHour(5).withMinute(0);
            end1 = now.minusDays(1).withHour(23).withMinute(59);
            start2 = now.withHour(0).withMinute(0);
            end2 = now.withHour(4).withMinute(59);
        } else {
            start1 = now.withHour(5).withMinute(0);
            end1 = now.withHour(23).withMinute(59);
            start2 = now.plusDays(1).withHour(0).withMinute(0);
            end2 = now.plusDays(1).withHour(4).withMinute(59);
        }

        return isInRange(target, start1, end1) || isInRange(target, start2, end2);
    }

    /*
    아침 am 5:00 ~ am 11:59
    */
    public Boolean isMorning(LocalDateTime target) {
        return target.getHour() >= 5 && target.getHour() <= 11;
    }

    /*
    오후 pm 12:00 ~ pm 18:59
    */
    public Boolean isAfternoon(LocalDateTime target) {
        return target.getHour() >= 12 && target.getHour() <= 18;
    }

    /*
    밤 pm 19:00 ~ am 4:59
    */
    public Boolean isNight(LocalDateTime target) {
        return target.getHour() >= 19 || target.getHour() <= 4;
    }

    /*
    target이 이번 달의 날짜에 속하는지
    */
    public Boolean isCurrentMonth(LocalDateTime target) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), 1, 0, 0);
        LocalDateTime end = getLastDayOfMonth(now);
        return isInRange(target, start, end);
    }

    /*
    target 월의 마지막 날
    */
    public LocalDateTime getLastDayOfMonth(LocalDateTime target) {
        if (target.getMonth().getValue() == 12) {
            return LocalDateTime.of(target.getYear() + 1, 1, 1, 23, 59).minusDays(1);
        } else {
            return LocalDateTime.of(target.getYear(), target.getMonth().getValue() + 1, 1, 23, 59).minusDays(1);
        }
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

    /*
    HH, MM 시간값 유효한지 체크
    */
    public void isTimeValid(String HH, String mm) {
        int hour = Integer.parseInt(HH);
        int minute = Integer.parseInt(mm);

        if (hour < 0 || hour > 23) throw new MytaminException(DATETIME_PARSE_ERROR);
        if (minute < 0 || minute > 59) throw new MytaminException(DATETIME_PARSE_ERROR);
    }

    /*
    Report 수정 가능 여부
    */
    public Boolean canEditReport(Report report) {
        return isInRange(LocalDateTime.now(), report.getCreatedAt(), report.getCreatedAt().plusDays(1));
    }

    /*
    Care 수정 가능 여부
    */
    public Boolean canEditCare(Care care) {
        return isInRange(LocalDateTime.now(), care.getCreatedAt(), care.getCreatedAt().plusDays(1));
    }

    // 특정 시간 범위 내에 있는지
    public Boolean isInRange(LocalDateTime target, LocalDateTime start, LocalDateTime end) {
        return target.compareTo(start) >= 0 && end.compareTo(target) >= 0;
    }

}
