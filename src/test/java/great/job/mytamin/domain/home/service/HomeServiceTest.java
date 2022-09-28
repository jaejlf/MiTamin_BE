package great.job.mytamin.domain.home.service;

import great.job.mytamin.domain.home.dto.response.ActionResponse;
import great.job.mytamin.domain.home.dto.response.ActiveResponse;
import great.job.mytamin.domain.home.dto.response.WelcomeResponse;
import great.job.mytamin.global.service.TimeService;
import great.job.mytamin.global.support.CommonServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Home 서비스")
class HomeServiceTest extends CommonServiceTest {

    @Autowired
    private HomeService homeService;

    @Autowired
    private TimeService timeService;

    @DisplayName("홈 화면 웰컴 메세지")
    @Test
    void welcome() {
        //given & when
        WelcomeResponse result = homeService.welcome(user);

        //then
        String expectedComment = getExpectedComment();
        WelcomeResponse expected = WelcomeResponse.of(
                user.getNickname(),
                expectedComment
        );
        assertAll(
                () -> assertThat(result.getNickname()).isEqualTo(expected.getNickname()),
                () -> assertThat(result.getComment()).isEqualTo(expected.getComment())
        );
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
        assertThat(result.getUpdatedTime()).isEqualTo(result.getUpdatedTime());
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
        assertThat(result.getUpdatedTime()).isEqualTo(result.getUpdatedTime());
    }

    @DisplayName("행동에 따른 버튼 활성화 여부")
    @Test
    void getProgressStatus() {
        //given
        user.updateBreathTime();
        user.updateSenseTime();

        // when
        ActiveResponse result = homeService.getProgressStatus(user);

        //then
        ActiveResponse expected = ActiveResponse.of(
                true,
                true,
                false,
                false
        );
        assertAll(
                () -> assertThat(result.isBreathIsDone()).isEqualTo(expected.isBreathIsDone()),
                () -> assertThat(result.isSenseIsDone()).isEqualTo(expected.isSenseIsDone()),
                () -> assertThat(result.isReportIsDone()).isEqualTo(expected.isReportIsDone()),
                () -> assertThat(result.isCareIsDone()).isEqualTo(expected.isCareIsDone())
        );
    }

    private String getExpectedComment() {
        LocalDateTime now = LocalDateTime.now();

        // morning range
        LocalDateTime mStart = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 5, 0);
        LocalDateTime mend = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 11, 59);

        // afternoon range
        LocalDateTime aStart = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 12, 0);
        LocalDateTime aEnd = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 18, 59);

        // night range
        LocalDateTime nStart1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 19, 0);
        LocalDateTime nEnd1 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 23, 59);
        LocalDateTime nStart2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 0, 0);
        LocalDateTime nEnd2 = LocalDateTime.of(now.getYear(), now.getMonth().getValue(), now.getDayOfMonth(), 4, 59);

        if (timeService.isMorning(now)) return "오늘도 힘차게 시작해볼까요 ?";
        if (timeService.isAfternoon(now)) return "어떤 하루를 보내고 계신가요 ?";
        if (timeService.isNight(now)) return "푹 쉬고 내일 만나요";

        return "*** 이 메세지가 뜬다면 시간 설정 오류 ***";
    }

}