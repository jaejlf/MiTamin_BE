package great.job.mytamin.topic.home.service;

import great.job.mytamin.global.support.CommonServiceTest;
import great.job.mytamin.global.util.TimeUtil;
import great.job.mytamin.topic.home.dto.response.ActiveResponse;
import great.job.mytamin.topic.home.dto.response.WelcomeResponse;
import great.job.mytamin.topic.mytamin.entity.Care;
import great.job.mytamin.topic.mytamin.enumerate.CareCategory;
import great.job.mytamin.topic.mytamin.service.MytaminService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static great.job.mytamin.topic.home.enumerate.WelcomeComment.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("Home 서비스")
class HomeServiceTest extends CommonServiceTest {

    @Autowired
    private HomeService homeService;

    @MockBean
    private TimeUtil timeUtil;

    @MockBean
    private MytaminService mytaminService;

    @Nested
    @DisplayName("홈 화면 웰컴 메세지")
    class WelcomeTest {

        @DisplayName("마이타민 O")
        @Test
        void welcome_case1() {
            //given
            given(mytaminService.findMytamin(any(), any())).willReturn(mytamin);

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
            given(timeUtil.isMorning(any())).willReturn(true);
            given(timeUtil.isAfternoon(any())).willReturn(false);
            given(timeUtil.isNight(any())).willReturn(false);

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
            given(timeUtil.isMorning(any())).willReturn(false);
            given(timeUtil.isAfternoon(any())).willReturn(true);
            given(timeUtil.isNight(any())).willReturn(false);

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
            given(timeUtil.isMorning(any())).willReturn(false);
            given(timeUtil.isAfternoon(any())).willReturn(false);
            given(timeUtil.isNight(any())).willReturn(true);

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

    @DisplayName("행동에 따른 버튼 활성화 여부")
    @Test
    void getProgressStatus() {
        //given
        saveNewCare();

        given(timeUtil.isToday(any())).willReturn(true);
        given(mytaminService.findMytamin(any(), any())).willReturn(mytamin);

        // when
        ActiveResponse result = homeService.getProgressStatus(user);

        //then
        ActiveResponse expected = ActiveResponse.builder()
                .breathIsDone(true)
                .senseIsDone(true)
                .reportIsDone(false)
                .careIsDone(true)
                .build();

        assertAll(
                () -> assertThat(result.isBreathIsDone()).isEqualTo(expected.isBreathIsDone()),
                () -> assertThat(result.isSenseIsDone()).isEqualTo(expected.isSenseIsDone()),
                () -> assertThat(result.isReportIsDone()).isEqualTo(expected.isReportIsDone()),
                () -> assertThat(result.isCareIsDone()).isEqualTo(expected.isCareIsDone())
        );
    }

    private void saveNewCare() {
        Care care = new Care(
                CareCategory.getMsgToCode(1),
                "hi",
                "hello",
                mytamin
        );
        careRepository.save(care);
        mytamin.updateCare(care);
    }

}