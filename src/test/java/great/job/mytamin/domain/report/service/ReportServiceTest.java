package great.job.mytamin.domain.report.service;

import great.job.mytamin.domain.mytamin.service.MytaminService;
import great.job.mytamin.domain.report.dto.request.ReportRequest;
import great.job.mytamin.domain.report.dto.response.ReportResponse;
import great.job.mytamin.domain.report.entity.Report;
import great.job.mytamin.domain.report.enumerate.MentalCondition;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.service.TimeService;
import great.job.mytamin.global.support.CommonServiceTest;
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
    private TimeService timeService;

    @MockBean
    private MytaminService mytaminService;

    @Nested
    @DisplayName("하루 진단하기")
    class ReportTodayTest {

        @DisplayName("성공")
        @Test
        void reportToday() {
            //given
            ReportRequest reportRequest = new ReportRequest(
                    5,
                    "신나는",
                    "즐거운",
                    "재밌는",
                    "아무래도 아침형 인간이 되는건 너무 어려운 것 같다."
            );
            given(timeService.convertToTakeAt(any())).willReturn(mockTakeAtNow);
            given(mytaminService.getMytamin(any(), any())).willReturn(mytamin);

            //when
            ReportResponse result = reportService.reportToday(user, reportRequest);

            //then
            ReportResponse expected = ReportResponse.of(
                    new Report(
                            MentalCondition.VERY_GOOD.getMsg(),
                            "신나는",
                            "즐거운",
                            "재밌는",
                            "아무래도 아침형 인간이 되는건 너무 어려운 것 같다.",
                            mytamin
                    )
            );
            assertAll(
                    () -> assertThat(result.getMentalCondition()).isEqualTo(expected.getMentalCondition()),
                    () -> assertThat(result.getFeelingTag()).isEqualTo("#신나는 #즐거운 #재밌는"),
                    () -> assertThat(result.getTodayReport()).isEqualTo(expected.getTodayReport())
            );
        }

        @DisplayName("이미 하루 진단 완료")
        @Test
        void reportToday_4001() {
            //given
            ReportRequest reportRequest = new ReportRequest(
                    5,
                    "신나는",
                    "즐거운",
                    "재밌는",
                    "아무래도 아침형 인간이 되는건 너무 어려운 것 같다."
            );
            given(timeService.convertToTakeAt(any())).willReturn(mockTakeAtNow);
            given(mytaminService.getMytamin(any(), any())).willReturn(mytamin);

            mytamin.updateReport(report);

            //when & then
            assertThatThrownBy(() -> reportService.reportToday(user, reportRequest))
                    .isInstanceOf(MytaminException.class)
                    .hasMessageContaining("REPORT_ALREADY_DONE");
        }

    }

}