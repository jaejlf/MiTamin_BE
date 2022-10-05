package great.job.mytamin.domain.mytamin.service;

import great.job.mytamin.domain.mytamin.dto.response.MytaminResponse;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.global.service.TimeService;
import great.job.mytamin.global.support.CommonServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("Mytamin 서비스")
class MytaminServiceTest extends CommonServiceTest {

    @Autowired
    private MytaminService mytaminService;

    @MockBean
    private TimeService timeService;

    @DisplayName("최근 섭취한 마이타민")
    @Test
    void getLatestMytamin() {
        //given
        mytaminRepository.save(mytamin);
        reportRepository.save(report);
        careRepository.save(care);
        mytamin.updateReport(report);
        mytamin.updateCare(care);
        given(timeService.isInRange(any(), any(), any())).willReturn(true);

        //when
        MytaminResponse result = mytaminService.getLatestMytamin(user);

        //then
        MytaminResponse expected = MytaminResponse.of(
                mytamin, 5, true
        );
        assertAll(
                () -> assertThat(result.isCanEdit()).isEqualTo(expected.isCanEdit()),
                () -> assertThat(result.getMemtalConditionCode()).isEqualTo(expected.getMemtalConditionCode()),
                () -> assertThat(result.getFeelingTag()).isEqualTo(expected.getFeelingTag()),
                () -> assertThat(result.getMentalConditionMsg()).isEqualTo(expected.getMentalConditionMsg()),
                () -> assertThat(result.getTodayReport()).isEqualTo(expected.getTodayReport()),
                () -> assertThat(result.getCareMsg1()).isEqualTo(expected.getCareMsg1()),
                () -> assertThat(result.getCareMsg2()).isEqualTo(expected.getCareMsg2())
        );
    }

    @DisplayName("마이타민 가져오기")
    @Test
    void getMytamin() {
        //given & when
        Mytamin result = mytaminService.getMytamin(user, mockTakeAtNow);

        //then
        Mytamin expected = mytamin;
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("마이타민 생성")
    @Test
    void createMytamin() {
        //given
        given(timeService.convertToTakeAt(any())).willReturn(mockTakeAtNow);

        //when
        Mytamin result = mytaminService.createMytamin(user, LocalDateTime.now());

        //then
        assertAll(
                () -> assertThat(result.getTakeAt()).isEqualTo(mockTakeAtNow),
                () -> assertThat(result).isNotNull()
        );
    }

}