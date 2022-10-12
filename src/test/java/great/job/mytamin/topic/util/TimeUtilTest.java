package great.job.mytamin.topic.util;

import great.job.mytamin.global.support.CommonServiceTest;
import great.job.mytamin.topic.util.TimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Time 유틸")
class TimeUtilTest extends CommonServiceTest {

    @Autowired
    private TimeUtil timeUtil;

    @DisplayName("LocalDateTime -> takeAt 포맷으로 변환")
    @ParameterizedTest
    @CsvSource({
            "2022-09-05T10:00, 2022.09.05.Mon", // day1
            "2022-09-13T23:59, 2022.09.13.Tue", // day1
            "2022-09-23T00:00, 2022.09.22.Thu", // day2
            "2022-09-25T00:34, 2022.09.24.Sat", // day2
            "2022-09-27T05:00, 2022.09.27.Tue", // day1
    })
    void convertToTakeAt(LocalDateTime target, String expected) {
        //given & when
        String result = timeUtil.convertToTakeAt(target);

        //then
        assertThat(result).isEqualTo(expected);
    }

//    // LocalDateTime.now() 9.28 기준 - 성공
//    @DisplayName("target이 오늘의 범위에 속하는지")
//    @ParameterizedTest
//    @CsvSource({
//            "2022-09-28T04:59, false",
//            "2022-09-28T05:00, true",
//            "2022-09-28T11:59, true",
//            "2022-09-29T00:00, true",
//            "2022-09-29T04:59, true"
//    })
//    void isToday(LocalDateTime target, boolean expected) {
//        //given & when
//        boolean result = timeService.isToday(target);
//
//        //then
//        assertThat(result).isEqualTo(expected);
//    }

    @DisplayName("아침 am 5:00 ~ am 11:59")
    @ParameterizedTest
    @CsvSource({
            "2022-09-28T04:59, false",
            "2022-09-28T05:00, true",
            "2022-09-28T11:59, true",
            "2022-09-28T12:00, false",
    })
    void isMorning(LocalDateTime target, boolean expected) {
        //given & when
        boolean result = timeUtil.isMorning(target);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("오후 pm 12:00 ~ pm 18:59")
    @ParameterizedTest
    @CsvSource({
            "2022-09-28T11:59, false",
            "2022-09-28T12:00, true",
            "2022-09-28T18:59, true",
            "2022-09-28T19:00, false",
    })
    void isAfternoon(LocalDateTime target, boolean expected) {
        //given & when
        boolean result = timeUtil.isAfternoon(target);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("밤 pm 19:00 ~ am 4:59")
    @ParameterizedTest
    @CsvSource({
            "2022-09-28T18:59, false",
            "2022-09-28T19:00, true",
            "2022-09-29T04:59, true",
            "2022-09-29T05:00, false",
    })
    void isNight(LocalDateTime target, boolean expected) {
        //given & when
        boolean result = timeUtil.isNight(target);

        //then
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("특정 시간 범위 내에 있는지")
    @ParameterizedTest
    @CsvSource({
            "2022-09-28T18:59, 2022-09-28T19:00, 2022-09-28T20:00, false",
            "2022-09-28T00:00, 2022-09-27T23:00, 2022-09-29T04:00, true",
    })
    void isInRange(LocalDateTime target, LocalDateTime start, LocalDateTime end, boolean expected) {
        //given & when
        boolean result = timeUtil.isInRange(target, start, end);

        //then
        assertThat(result).isEqualTo(expected);
    }

}