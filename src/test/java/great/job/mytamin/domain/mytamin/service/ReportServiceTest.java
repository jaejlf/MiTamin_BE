package great.job.mytamin.domain.mytamin.service;

import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonServiceTest;
import great.job.mytamin.domain.util.ReportUtil;
import great.job.mytamin.domain.util.TimeUtil;
import great.job.mytamin.domain.mytamin.dto.request.ReportRequest;
import great.job.mytamin.domain.mytamin.dto.response.ReportResponse;
import great.job.mytamin.domain.mytamin.enumerate.MentalCondition;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("Report 서비스")
class ReportServiceTest extends CommonServiceTest {

    @Autowired
    private ReportService reportService;

    @MockBean
    private ReportUtil reportUtil;

    @MockBean
    private TimeUtil timeUtil;

    @MockBean
    private MytaminService mytaminService;

    @Nested
    @DisplayName("하루 진단하기")
    class CreateReportTest {

        @DisplayName("성공")
        @Test
        void createReport() {
            //given
            ReportRequest reportRequest = new ReportRequest(
                    5,
                    "신나는",
                    "즐거운",
                    "재밌는",
                    "아무래도 아침형 인간이 되는건 너무 어려운 것 같다."
            );

            given(mytaminService.findMytaminOrNew(any())).willReturn(mytamin);
            given(reportUtil.concatFeelingTag(any())).willReturn("#신나는 #즐거운 #재밌는");
            given(timeUtil.canEditReport(any())).willReturn(true);

            //when
            ReportResponse result = reportService.createReport(user, reportRequest);

            //then
            ReportResponse expected = ReportResponse.builder()
                    .reportId(1L)
                    .canEdit(true)
                    .mentalConditionCode(5)
                    .mentalCondition(MentalCondition.VERY_GOOD.getMsg())
                    .feelingTag("#신나는 #즐거운 #재밌는")
                    .todayReport("아무래도 아침형 인간이 되는건 너무 어려운 것 같다.")
                    .build();

            assertAll(
                    () -> assertThat(result.getCanEdit()).isEqualTo(expected.getCanEdit()),
                    () -> assertThat(result.getMentalCondition()).isEqualTo(expected.getMentalCondition()),
                    () -> assertThat(result.getFeelingTag()).isEqualTo(expected.getFeelingTag()),
                    () -> assertThat(result.getTodayReport()).isEqualTo(expected.getTodayReport())
            );
        }

        @DisplayName("이미 하루 진단 완료")
        @Test
        void createReport_4001() {
            //given
            ReportRequest reportRequest = new ReportRequest(
                    5,
                    "신나는",
                    "즐거운",
                    "재밌는",
                    "아무래도 아침형 인간이 되는건 너무 어려운 것 같다."
            );

            given(mytaminService.findMytaminOrNew(any())).willReturn(mytamin);
            given(reportUtil.concatFeelingTag(any())).willReturn("#신나는 #즐거운 #재밌는");
            given(timeUtil.canEditReport(any())).willReturn(true);

            mytamin.updateReport(report);

            //when & then
            assertThatThrownBy(() -> reportService.createReport(user, reportRequest))
                    .isInstanceOf(MytaminException.class)
                    .hasMessageContaining("REPORT_ALREADY_DONE");
        }

    }

}