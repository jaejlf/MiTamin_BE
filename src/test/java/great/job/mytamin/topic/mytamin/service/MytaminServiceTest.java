package great.job.mytamin.topic.mytamin.service;

import great.job.mytamin.topic.mytamin.dto.response.CareResponse;
import great.job.mytamin.topic.mytamin.dto.response.MytaminResponse;
import great.job.mytamin.topic.mytamin.entity.Mytamin;
import great.job.mytamin.topic.mytamin.dto.response.ReportResponse;
import great.job.mytamin.topic.mytamin.enumerate.MentalCondition;
import great.job.mytamin.global.support.CommonServiceTest;
import great.job.mytamin.global.util.ReportUtil;
import great.job.mytamin.global.util.TimeUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("Mytamin 서비스")
class MytaminServiceTest extends CommonServiceTest {

    @Autowired
    private MytaminService mytaminService;

    @Autowired
    private ReportUtil reportUtil;

    @MockBean
    private TimeUtil timeUtil;

    @DisplayName("숨 고르기 완료")
    @Test
    void completeBreath() {
        //given & when
        mytaminService.completeBreath(user);
        String result = user.getBreathTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));

        //then
        String expected = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        assertThat(result).isEqualTo(expected);
    }

    @DisplayName("감각 깨우기 완료")
    @Test
    void completeSense() {
        //given & when
        mytaminService.completeSense(user);
        String result = user.getSenseTime().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));

        //then
        String expected = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm"));
        assertThat(result).isEqualTo(expected);
    }
    
    @DisplayName("최근 섭취한 마이타민")
    @Test
    void getLatestMytamin() {
        //given
        saveReportAndCare();
        given(timeUtil.isInRange(any(), any(), any())).willReturn(true);

        //when
        MytaminResponse result = mytaminService.getLatestMytamin(user);

        //then
        MytaminResponse expected = MytaminResponse.builder()
                .takeAt(mytamin.getTakeAt())
                .canEditReport(true)
                .canEditCare(true)
                .report(mockReportResponse())
                .care(mockCareResponse())
                .build();

        assertAll(
                () -> assertThat(result.getTakeAt()).isEqualTo(expected.getTakeAt()),
                () -> assertThat(result.isCanEditReport()).isEqualTo(expected.isCanEditReport()),
                () -> assertThat(result.isCanEditCare()).isEqualTo(expected.isCanEditCare()),
                () -> assertThat(result.getReport()).isNotNull(),
                () -> assertThat(result.getCare()).isNotNull()
        );
    }

//    @DisplayName("마이타민 가져오기")
//    @Test
//    void getMytamin() {
//        //given & when
//        Mytamin result = mytaminService.getMytamin(user, LocalDateTime.now());
//        given(timeUtil.convertToTakeAt(any())).willReturn(mockTakeAtNow);
//
//        //then
//        Mytamin expected = mytamin;
//        assertThat(result).isEqualTo(expected);
//    }

    @DisplayName("마이타민 생성")
    @Test
    void createMytamin() {
        //given
        given(timeUtil.convertToTakeAt(any())).willReturn(mockTakeAtNow);

        //when
        Mytamin result = mytaminService.createMytamin(user, LocalDateTime.now());

        //then
        assertAll(
                () -> assertThat(result.getTakeAt()).isEqualTo(mockTakeAtNow),
                () -> assertThat(result).isNotNull()
        );
    }

    private void saveReportAndCare() {
        reportRepository.save(report);
        careRepository.save(care);
        mytamin.updateReport(report);
        mytamin.updateCare(care);
    }

    private ReportResponse mockReportResponse() {
        return ReportResponse.builder()
                .mentalConditionCode(5)
                .mentalCondition(MentalCondition.VERY_GOOD.getMsg())
                .feelingTag("#신나는 #즐거운 #재밌는")
                .todayReport("아무래도 아침형 인간이 되는건 너무 어려운 것 같다.")
                .build();
    }

    private CareResponse mockCareResponse() {
        return CareResponse.builder()
                .careCategory("이루어 낸 일")
                .careMsg1("오늘 할 일을 전부 했어")
                .careMsg2("성실히 노력하는 내 모습이 좋아")
                .build();
    }

}