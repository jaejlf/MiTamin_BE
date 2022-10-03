package great.job.mytamin.domain.home.service;

import great.job.mytamin.domain.care.entity.Care;
import great.job.mytamin.domain.care.enumerate.CareCategory;
import great.job.mytamin.domain.home.dto.response.ActionResponse;
import great.job.mytamin.domain.home.dto.response.ActiveResponse;
import great.job.mytamin.domain.home.dto.response.WelcomeResponse;
import great.job.mytamin.domain.mytamin.service.MytaminService;
import great.job.mytamin.global.service.TimeService;
import great.job.mytamin.global.support.CommonServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static great.job.mytamin.domain.mytamin.enumerate.WelcomeComment.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("Home 서비스")
class HomeServiceTest extends CommonServiceTest {

    @Autowired
    private HomeService homeService;

    @MockBean
    private TimeService timeService;

    @MockBean
    private MytaminService mytaminService;

    @Nested
    @DisplayName("홈 화면 웰컴 메세지")
    class WelcomeTest {

        @DisplayName("마이타민 O")
        @Test
        void welcome_case1() {
            //given
            given(timeService.convertToTakeAt(any())).willReturn(mockTakeAtNow);
            given(mytaminService.getMytamin(any(), any())).willReturn(mytamin);

            // when
            WelcomeResponse result = homeService.welcome(user);

            //then
            String expectedComment = TAKE_MYTAMIN_DONE.getComment();
            WelcomeResponse expected = WelcomeResponse.of(
                    user.getNickname(),
                    expectedComment
            );
            assertAll(
                    () -> assertThat(result.getNickname()).isEqualTo(expected.getNickname()),
                    () -> assertThat(result.getComment()).isEqualTo(expected.getComment())
            );
        }

        @DisplayName("마이타민 X / 지정 시간 X / 아침")
        @Test
        void welcome_case2() {
            //given
            given(timeService.isMorning(any())).willReturn(true);
            given(timeService.isAfternoon(any())).willReturn(false);
            given(timeService.isNight(any())).willReturn(false);

            // when
            WelcomeResponse result = homeService.welcome(user);

            //then
            String expectedComment = MORNING.getComment();
            WelcomeResponse expected = WelcomeResponse.of(
                    user.getNickname(),
                    expectedComment
            );
            assertAll(
                    () -> assertThat(result.getNickname()).isEqualTo(expected.getNickname()),
                    () -> assertThat(result.getComment()).isEqualTo(expected.getComment())
            );
        }

        @DisplayName("마이타민 X / 지정 시간 X / 오후")
        @Test
        void welcome_case3() {
            //given
            given(timeService.isMorning(any())).willReturn(false);
            given(timeService.isAfternoon(any())).willReturn(true);
            given(timeService.isNight(any())).willReturn(false);

            // when
            WelcomeResponse result = homeService.welcome(user);

            //then
            String expectedComment = AFTERNOON.getComment();
            WelcomeResponse expected = WelcomeResponse.of(
                    user.getNickname(),
                    expectedComment
            );
            assertAll(
                    () -> assertThat(result.getNickname()).isEqualTo(expected.getNickname()),
                    () -> assertThat(result.getComment()).isEqualTo(expected.getComment())
            );
        }

        @DisplayName("마이타민 X / 지정 시간 X / 밤")
        @Test
        void welcome_case4() {
            //given
            given(timeService.isMorning(any())).willReturn(false);
            given(timeService.isAfternoon(any())).willReturn(false);
            given(timeService.isNight(any())).willReturn(true);

            // when
            WelcomeResponse result = homeService.welcome(user);

            //then
            String expectedComment = NIGHT.getComment();
            WelcomeResponse expected = WelcomeResponse.of(
                    user.getNickname(),
                    expectedComment
            );
            assertAll(
                    () -> assertThat(result.getNickname()).isEqualTo(expected.getNickname()),
                    () -> assertThat(result.getComment()).isEqualTo(expected.getComment())
            );
        }

        @DisplayName("마이타민 X / 지정 시간 O / 2시간 전")
        @Test
        void welcome_case5() {
        }

        @DisplayName("마이타민 X / 지정 시간 O / 2시간 후")
        @Test
        void welcome_case6() {
        }

        @DisplayName("마이타민 X / 지정 시간 O / 2시간 전 / 하루 경계")
        @Test
        void welcome_case7() {
        }

        @DisplayName("마이타민 X / 지정 시간 O / 2시간 후 / 하루 경계")
        @Test
        void welcome_case8() {
        }

    }

    @DisplayName("숨 고르기 완료")
    @Test
    void completeBreath() {
        //given & when
        ActionResponse result = homeService.completeBreath(user);

        //then
        ActionResponse expected = ActionResponse.of(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
        );
        assertThat(result.getUpdatedTime()).isEqualTo(expected.getUpdatedTime());
    }

    @DisplayName("감각 깨우기 완료")
    @Test
    void completeSense() {
        //given & when
        ActionResponse result = homeService.completeSense(user);

        //then
        ActionResponse expected = ActionResponse.of(
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"))
        );
        assertThat(result.getUpdatedTime()).isEqualTo(expected.getUpdatedTime());
    }

    @DisplayName("행동에 따른 버튼 활성화 여부")
    @Test
    void getProgressStatus() {
        //given
        Care care = new Care(CareCategory.getMsgToCode(1), "hi", "hello", mytamin);
        careRepository.save(care);
        mytamin.updateCare(care);

        given(timeService.isToday(any())).willReturn(true);
        given(mytaminService.getMytamin(any(), any())).willReturn(mytamin);

        // when
        ActiveResponse result = homeService.getProgressStatus(user);

        //then
        ActiveResponse expected = ActiveResponse.of(
                true,
                true,
                false,
                true
        );
        assertAll(
                () -> assertThat(result.isBreathIsDone()).isEqualTo(expected.isBreathIsDone()),
                () -> assertThat(result.isSenseIsDone()).isEqualTo(expected.isSenseIsDone()),
                () -> assertThat(result.isReportIsDone()).isEqualTo(expected.isReportIsDone()),
                () -> assertThat(result.isCareIsDone()).isEqualTo(expected.isCareIsDone())
        );
    }

}