package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.dto.request.ReportRequest;
import great.job.mytamin.domain.mytamin.dto.response.FeelingRankResponse;
import great.job.mytamin.domain.mytamin.dto.response.ReportResponse;
import great.job.mytamin.domain.mytamin.dto.response.WeeklyMentalReportResponse;
import great.job.mytamin.domain.mytamin.enumerate.MentalCondition;
import great.job.mytamin.domain.mytamin.service.ReportService;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static great.job.mytamin.global.exception.ErrorMap.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ReportController.class)
@DisplayName("Report 컨트롤러")
class ReportControllerTest extends CommonControllerTest {

    String docId = "/report/";

    @MockBean
    private ReportService reportService;

    @Nested
    @DisplayName("하루 진단하기")
    class CreateReportTest {

        ReportRequest reportRequest = new ReportRequest(
                5,
                "신나는",
                "즐거운",
                "재밌는",
                "아무래도 아침형 인간이 되는건 너무 어려운 것 같다."
        );

        @DisplayName("성공")
        @Test
        void createReport(TestInfo testInfo) throws Exception {
            //given
            given(reportService.createReport(any(), any())).willReturn(
                    ReportResponse.builder()
                            .reportId(1L)
                            .canEdit(true)
                            .mentalConditionCode(5)
                            .mentalCondition(MentalCondition.VERY_GOOD.getMsg())
                            .feelingTag("#신나는 #즐거운 #재밌는")
                            .todayReport("아무래도 아침형 인간이 되는건 너무 어려운 것 같다.")
                            .build()
            );

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
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
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
                                    fieldWithPath("data.reportId").description("하루 진단 id"),
                                    fieldWithPath("data.canEdit").description("'하루 진단' 수정 가능 여부"),
                                    fieldWithPath("data.mentalConditionCode").description("마음 컨디션 코드"),
                                    fieldWithPath("data.mentalCondition").description("마음 컨디션 메세지"),
                                    fieldWithPath("data.feelingTag").description("감정 태그"),
                                    fieldWithPath("data.todayReport").description("하루 진단")
                            ))
                    );
        }

        @DisplayName("이미 하루 진단 완료")
        @Test
        void createReport_4001(TestInfo testInfo) throws Exception {
            //given
            given(reportService.createReport(any(), any())).willThrow(new MytaminException(REPORT_ALREADY_DONE_ERROR));

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
                    .andExpect(jsonPath("errorName").value("REPORT_ALREADY_DONE_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
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
        void createReport_4000(TestInfo testInfo) throws Exception {
            //given
            given(reportService.createReport(any(), any())).willThrow(new MytaminException(INVALID_CONDITION_CODE_ERROR));

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
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("하루 진단 조회")
    class GetReportTest {

        Long reportId = 1L;

        @DisplayName("성공")
        @Test
        void getReport(TestInfo testInfo) throws Exception {
            //given
            given(reportService.getReport(any(), any())).willReturn(
                    ReportResponse.builder()
                            .reportId(1L)
                            .canEdit(true)
                            .mentalConditionCode(5)
                            .mentalCondition(MentalCondition.VERY_GOOD.getMsg())
                            .feelingTag("#신나는 #즐거운 #재밌는")
                            .todayReport("아무래도 아침형 인간이 되는건 너무 어려운 것 같다.")
                            .build()
            );

            //when
            ResultActions actions = mockMvc.perform(get("/report/{reportId}", reportId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("reportId").description("*하루 진단 id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.reportId").description("하루 진단 id"),
                                    fieldWithPath("data.canEdit").description("'하루 진단' 수정 가능 여부"),
                                    fieldWithPath("data.mentalConditionCode").description("마음 컨디션 코드"),
                                    fieldWithPath("data.mentalCondition").description("마음 컨디션 메세지"),
                                    fieldWithPath("data.feelingTag").description("감정 태그"),
                                    fieldWithPath("data.todayReport").description("하루 진단")
                            ))
                    );
        }

        @DisplayName("존재하지 않는 하루 진단 id")
        @Test
        void getReport_4002(TestInfo testInfo) throws Exception {
            //given
            given(reportService.getReport(any(), any())).willThrow(new MytaminException(REPORT_NOT_FOUND_ERROR));

            //when
            ResultActions actions = mockMvc.perform(get("/report/{reportId}", reportId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(4002))
                    .andExpect(jsonPath("errorName").value("REPORT_NOT_FOUND_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("하루 진단 수정")
    class UpdateReportTest {

        Long reportId = 1L;

        ReportRequest reportRequest = new ReportRequest(
                5,
                "신나는",
                "즐거운",
                "재밌는",
                "하루 진단 기록 수정 중..."
        );

        @DisplayName("성공")
        @Test
        void updateReport(TestInfo testInfo) throws Exception {
            //given
            doNothing().when(reportService).updateReport(any(), any(), any());

            //when
            ResultActions actions = mockMvc.perform(put("/report/{reportId}", reportId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(reportRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("reportId").description("*수정할 하루 진단 id")
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
                                    fieldWithPath("message").description("결과 메세지")
                            ))
                    );
        }

        @DisplayName("존재하지 않는 하루 진단 id")
        @Test
        void updateReport_4002(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(REPORT_NOT_FOUND_ERROR)).when(reportService).updateReport(any(), any(), any());

            //when
            ResultActions actions = mockMvc.perform(put("/report/{reportId}", reportId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(reportRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(4002))
                    .andExpect(jsonPath("errorName").value("REPORT_NOT_FOUND_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("수정 불가 시간")
        @Test
        void updateReport_7000(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(EDIT_TIMEOUT_ERROR)).when(reportService).updateReport(any(), any(), any());

            //when
            ResultActions actions = mockMvc.perform(put("/report/{reportId}", reportId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(reportRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(7000))
                    .andExpect(jsonPath("errorName").value("EDIT_TIMEOUT_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @DisplayName("주간 마음 컨디션 조회")
    @Test
    void getWeeklyMentalReport(TestInfo testInfo) throws Exception {
        //given
        given(reportService.getWeeklyMentalReport(any())).willReturn(mockWeeklyMentalResponseList());

        //when
        ResultActions actions = mockMvc.perform(get("/report/weekly/mental")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data[].dayOfWeek").description("요일"),
                                fieldWithPath("data[].mentalConditionCode").description("마음 컨디션 코드")
                        ))
                );
    }

    @DisplayName("이번 달 가장 많이 느낀 감정")
    @Test
    void getMonthlyFeelingRank(TestInfo testInfo) throws Exception {
        //given
        given(reportService.getMonthlyFeelingRank(any())).willReturn(mockFeelingRankResponse());

        //when
        ResultActions actions = mockMvc.perform(get("/report/feeling/rank")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data[].feeling").description("감정 태그"),
                                fieldWithPath("data[].count").description("감정이 기록된 횟수")
                        ))
                );
    }

    private static List<WeeklyMentalReportResponse> mockWeeklyMentalResponseList() {
        List<WeeklyMentalReportResponse> weeklyMentalReportResponseList = new ArrayList<>();
        weeklyMentalReportResponseList.add(WeeklyMentalReportResponse.of("수", 0));
        weeklyMentalReportResponseList.add(WeeklyMentalReportResponse.of("목", 3));
        weeklyMentalReportResponseList.add(WeeklyMentalReportResponse.of("금", 1));
        weeklyMentalReportResponseList.add(WeeklyMentalReportResponse.of("토", 0));
        weeklyMentalReportResponseList.add(WeeklyMentalReportResponse.of("일", 0));
        weeklyMentalReportResponseList.add(WeeklyMentalReportResponse.of("월", 3));
        weeklyMentalReportResponseList.add(WeeklyMentalReportResponse.of("화", 2));
        weeklyMentalReportResponseList.add(WeeklyMentalReportResponse.of("오늘", 5));
        return weeklyMentalReportResponseList;
    }

    private List<FeelingRankResponse> mockFeelingRankResponse() {
        List<FeelingRankResponse> feelingRankResponseList = new ArrayList<>();
        feelingRankResponseList.add(
                FeelingRankResponse.builder()
                        .feeling("신나는")
                        .count(5)
                        .build());
        feelingRankResponseList.add(
                FeelingRankResponse.builder()
                        .feeling("즐거운")
                        .count(3)
                        .build());
        feelingRankResponseList.add(
                FeelingRankResponse.builder()
                        .feeling("무념무상의")
                        .count(1)
                        .build());
        return feelingRankResponseList;
    }

}