package great.job.mytamin.global.service;

import great.job.mytamin.global.support.CommonServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Time 서비스")
class TimeServiceTest extends CommonServiceTest {

    @Autowired
    private TimeService timeService;

    LocalDateTime now = LocalDateTime.now();

    @DisplayName("LocalDateTime 포맷 변환")
    @Test
    void convertTimeFormat() {
        //given
        LocalDateTime target = LocalDateTime.of(2022, 9, 28, 11, 59);

        //when
        String result = timeService.convertTimeFormat(target);

        //then
        String expected = "2022.09.28.Wed";
        assertThat(result).isEqualTo(expected);
    }

    @Nested
    @DisplayName("24시간 이내 생성된 데이터인지")
    class Within24Test {

        @DisplayName("true")
        @Test
        void isCreatedAtWithin24_true() {
            //given
            LocalDateTime target = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), now.getHour() - 1, 0);

            //when
            boolean result = timeService.isCreatedAtWithin24(target);

            //then
            boolean expected = true;
            assertThat(result).isEqualTo(expected);
        }

        @DisplayName("false")
        @Test
        void isCreatedAtWithin24_false() {
            //given
            LocalDateTime target = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth() - 1, 9, 0);

            //when
            boolean result = timeService.isCreatedAtWithin24(target);

            //then
            boolean expected = false;
            assertThat(result).isEqualTo(expected);
        }

    }

    @Nested
    @DisplayName("하루의 범위 내에 있는지")
    class IsDayTest {

        @DisplayName("true")
        @Test
        void isDay_true() {
            //given
            LocalDateTime target = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), now.getHour() - 1, 0);

            //when
            boolean result = timeService.isDay(target);

            //then
            boolean expected = true;
            assertThat(result).isEqualTo(expected);
        }

        @DisplayName("false")
        @Test
        void isDay_false() {
            //given
            LocalDateTime target = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth() - 1, 9, 0);

            //when
            boolean result = timeService.isDay(target);

            //then
            boolean expected = false;
            assertThat(result).isEqualTo(expected);
        }

    }

    @Nested
    @DisplayName("현재 시간이 특정 시간 범위 내에 있는지")
    class IsInRangeTest {

        @DisplayName("아침")
        @Test
        void isMorning() {
            //given
            LocalDateTime target = LocalDateTime.of(2022, 9, 28, 9, 0);
            LocalDateTime start = LocalDateTime.of(target.getYear(), target.getMonth().getValue(), target.getDayOfMonth(), 5, 0);
            LocalDateTime end = LocalDateTime.of(target.getYear(), target.getMonth().getValue(), target.getDayOfMonth(), 11, 59);

            //when
            boolean result = timeService.isInRange(target, start, end);

            //then
            boolean expected = true;
            assertThat(result).isEqualTo(expected);
        }

        @DisplayName("오후")
        @Test
        void isAfternoon() {
            //given
            LocalDateTime target = LocalDateTime.of(2022, 9, 28, 15, 0);
            LocalDateTime start = LocalDateTime.of(target.getYear(), target.getMonth().getValue(), target.getDayOfMonth(), 12, 0);
            LocalDateTime end = LocalDateTime.of(target.getYear(), target.getMonth().getValue(), target.getDayOfMonth(), 18, 59);

            //when
            boolean result = timeService.isInRange(target, start, end);

            //then
            boolean expected = true;
            assertThat(result).isEqualTo(expected);
        }

        @DisplayName("밤")
        @Test
        void isNight() {
            //given
            LocalDateTime target = LocalDateTime.of(2022, 9, 28, 0, 0);
            LocalDateTime start1 = LocalDateTime.of(target.getYear(), target.getMonth().getValue(), target.getDayOfMonth(), 19, 0);
            LocalDateTime end1 = LocalDateTime.of(target.getYear(), target.getMonth().getValue(), target.getDayOfMonth(), 23, 59);
            LocalDateTime start2 = LocalDateTime.of(target.getYear(), target.getMonth().getValue(), target.getDayOfMonth(), 0, 0);
            LocalDateTime end2 = LocalDateTime.of(target.getYear(), target.getMonth().getValue(), target.getDayOfMonth(), 4, 59);

            //when
            boolean result = timeService.isInRange(target, start1, end1) || timeService.isInRange(target, start2, end2);

            //then
            boolean expected = true;
            assertThat(result).isEqualTo(expected);
        }

    }

}