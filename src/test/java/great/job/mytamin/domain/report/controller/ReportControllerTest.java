package great.job.mytamin.domain.report.controller;

import great.job.mytamin.domain.report.dto.request.ReportRequest;
import great.job.mytamin.domain.report.dto.response.ReportResponse;
import great.job.mytamin.domain.report.entity.Report;
import great.job.mytamin.domain.report.enumerate.MentalCondition;
import great.job.mytamin.domain.report.service.ReportService;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static great.job.mytamin.global.exception.ErrorMap.INVALID_CONDITION_CODE_ERROR;
import static great.job.mytamin.global.exception.ErrorMap.REPORT_ALREADY_DONE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReportController.class)
@DisplayName("Report 컨트롤러")
class ReportControllerTest extends CommonControllerTest {

    @MockBean
    private ReportService reportService;

    @Nested
    @DisplayName("하루 진단하기")
    class ReportTodayTest {

        @DisplayName("성공")
        @Test
        void reportToday(TestInfo testInfo) throws Exception {
            //given
            ReportRequest reportRequest = new ReportRequest(
                    5,
                    "신나는",
                    "즐거운",
                    "재밌는",
                    "아무래도 아침형 인간이 되는건 너무 어려운 것 같다."
            );
            given(reportService.reportToday(any(), any())).willReturn(ReportResponse.of(
                    new Report(
                            MentalCondition.VERY_GOOD.getMsg(),
                            "신나는",
                            "즐거운",
                            "재밌는",
                            "아무래도 아침형 인간이 되는건 너무 어려운 것 같다.",
                            mytamin
                    )
            ));

            //when
            ResultActions actions = mockMvc.perform(post("/report/new")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(reportRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document("/report/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("mentalConditionCode").description("*마음 컨디션 코드"),
                                    fieldWithPath("tag1").description("*감정 태그1"),
                                    fieldWithPath("tag2").description("감정 태그2"),
                                    fieldWithPath("tag3").description("감정 태그3"),
                                    fieldWithPath("todayReport").description("*하루 진단")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.takeAt").description("마이타민 섭취 날짜"),
                                    fieldWithPath("data.mentalCondition").description("마음 컨디션 메세지"),
                                    fieldWithPath("data.feelingTag").description("감정 태그"),
                                    fieldWithPath("data.todayReport").description("하루 진단")
                            ))
                    );
        }

        @DisplayName("이미 하루 진단 완료")
        @Test
        void reportToday_4001(TestInfo testInfo) throws Exception {
            //given
            ReportRequest reportRequest = new ReportRequest(
                    5,
                    "신나는",
                    "즐거운",
                    "재밌는",
                    "아무래도 아침형 인간이 되는건 너무 어려운 것 같다."
            );
            given(reportService.reportToday(any(), any())).willThrow(new MytaminException(REPORT_ALREADY_DONE));

            //when
            ResultActions actions = mockMvc.perform(post("/report/new")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(reportRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("errorCode").value(4001))
                    .andExpect(jsonPath("errorName").value("REPORT_ALREADY_DONE"))
                    .andDo(document("/report/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("mentalConditionCode").description("*마음 컨디션 코드"),
                                    fieldWithPath("tag1").description("*감정 태그1"),
                                    fieldWithPath("tag2").description("감정 태그2"),
                                    fieldWithPath("tag3").description("감정 태그3"),
                                    fieldWithPath("todayReport").description("*하루 진단")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("마음 컨디션 코드 오류")
        @Test
        void reportToday_4000(TestInfo testInfo) throws Exception {
            //given
            ReportRequest reportRequest = new ReportRequest(
                    5,
                    "신나는",
                    "즐거운",
                    "재밌는",
                    "아무래도 아침형 인간이 되는건 너무 어려운 것 같다."
            );
            given(reportService.reportToday(any(), any())).willThrow(new MytaminException(INVALID_CONDITION_CODE_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/report/new")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(reportRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(4000))
                    .andExpect(jsonPath("errorName").value("INVALID_CONDITION_CODE_ERROR"))
                    .andDo(document("/report/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("mentalConditionCode").description("*마음 컨디션 코드"),
                                    fieldWithPath("tag1").description("*감정 태그1"),
                                    fieldWithPath("tag2").description("감정 태그2"),
                                    fieldWithPath("tag3").description("감정 태그3"),
                                    fieldWithPath("todayReport").description("*하루 진단")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

}