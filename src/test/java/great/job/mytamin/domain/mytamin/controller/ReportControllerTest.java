package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.dto.request.ReportRequest;
import great.job.mytamin.domain.mytamin.dto.response.FeelingRankResponse;
import great.job.mytamin.domain.mytamin.dto.response.ReportResponse;
import great.job.mytamin.domain.mytamin.dto.response.WeeklyMentalConditionResponse;
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
@DisplayName("Report ????????????")
class ReportControllerTest extends CommonControllerTest {

    String docId = "/report/";

    @MockBean
    private ReportService reportService;

    @Nested
    @DisplayName("?????? ????????????")
    class CreateReportTest {

        ReportRequest reportRequest = new ReportRequest(
                5,
                "?????????",
                "?????????",
                "?????????",
                "???????????? ????????? ????????? ????????? ?????? ????????? ??? ??????."
        );

        @DisplayName("??????")
        @Test
        void createReport(TestInfo testInfo) throws Exception {
            //given
            given(reportService.createReport(any(), any())).willReturn(
                    ReportResponse.builder()
                            .reportId(1L)
                            .canEdit(true)
                            .mentalConditionCode(5)
                            .mentalCondition(MentalCondition.VERY_GOOD.getMsg())
                            .feelingTag("#????????? #????????? #?????????")
                            .todayReport("???????????? ????????? ????????? ????????? ?????? ????????? ??? ??????.")
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
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            requestFields(
                                    fieldWithPath("mentalConditionCode").description("*?????? ????????? ??????"),
                                    fieldWithPath("tag1").description("*?????? ??????1"),
                                    fieldWithPath("tag2").description("?????? ??????2"),
                                    fieldWithPath("tag3").description("?????? ??????3"),
                                    fieldWithPath("todayReport").description("*?????? ??????")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????"),
                                    fieldWithPath("data.reportId").description("?????? ?????? id"),
                                    fieldWithPath("data.canEdit").description("'?????? ??????' ?????? ?????? ??????"),
                                    fieldWithPath("data.mentalConditionCode").description("?????? ????????? ??????"),
                                    fieldWithPath("data.mentalCondition").description("?????? ????????? ?????????"),
                                    fieldWithPath("data.feelingTag").description("?????? ??????"),
                                    fieldWithPath("data.todayReport").description("?????? ??????")
                            ))
                    );
        }

        @DisplayName("?????? ?????? ?????? ??????")
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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("?????? ????????? ?????? ??????")
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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("?????? ?????? ??????")
    class GetReportTest {

        Long reportId = 1L;

        @DisplayName("??????")
        @Test
        void getReport(TestInfo testInfo) throws Exception {
            //given
            given(reportService.getReport(any(), any())).willReturn(
                    ReportResponse.builder()
                            .reportId(1L)
                            .canEdit(true)
                            .mentalConditionCode(5)
                            .mentalCondition(MentalCondition.VERY_GOOD.getMsg())
                            .feelingTag("#????????? #????????? #?????????")
                            .todayReport("???????????? ????????? ????????? ????????? ?????? ????????? ??? ??????.")
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
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            pathParameters(
                                    parameterWithName("reportId").description("*?????? ?????? id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????"),
                                    fieldWithPath("data.reportId").description("?????? ?????? id"),
                                    fieldWithPath("data.canEdit").description("'?????? ??????' ?????? ?????? ??????"),
                                    fieldWithPath("data.mentalConditionCode").description("?????? ????????? ??????"),
                                    fieldWithPath("data.mentalCondition").description("?????? ????????? ?????????"),
                                    fieldWithPath("data.feelingTag").description("?????? ??????"),
                                    fieldWithPath("data.todayReport").description("?????? ??????")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ?????? ?????? id")
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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("?????? ?????? ??????")
    class UpdateReportTest {

        Long reportId = 1L;

        ReportRequest reportRequest = new ReportRequest(
                5,
                "?????????",
                "?????????",
                "?????????",
                "?????? ?????? ?????? ?????? ???..."
        );

        @DisplayName("??????")
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
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            pathParameters(
                                    parameterWithName("reportId").description("*????????? ?????? ?????? id")
                            ),
                            requestFields(
                                    fieldWithPath("mentalConditionCode").description("*?????? ????????? ??????"),
                                    fieldWithPath("tag1").description("*?????? ??????1"),
                                    fieldWithPath("tag2").description("?????? ??????2"),
                                    fieldWithPath("tag3").description("?????? ??????3"),
                                    fieldWithPath("todayReport").description("*?????? ??????")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ?????? ?????? id")
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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("?????? ?????? ??????")
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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

    }

    @DisplayName("?????? ?????? ????????? ??????")
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
                                headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data[].dayOfWeek").description("??????"),
                                fieldWithPath("data[].mentalConditionCode").description("?????? ????????? ??????")
                        ))
                );
    }

    @DisplayName("?????? ??? ?????? ?????? ?????? ??????")
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
                                headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data[].feeling").description("?????? ??????"),
                                fieldWithPath("data[].count").description("????????? ????????? ??????")
                        ))
                );
    }

    private static List<WeeklyMentalConditionResponse> mockWeeklyMentalResponseList() {
        List<WeeklyMentalConditionResponse> weeklyMentalConditionResponseList = new ArrayList<>();
        weeklyMentalConditionResponseList.add(WeeklyMentalConditionResponse.of("???", 0));
        weeklyMentalConditionResponseList.add(WeeklyMentalConditionResponse.of("???", 3));
        weeklyMentalConditionResponseList.add(WeeklyMentalConditionResponse.of("???", 1));
        weeklyMentalConditionResponseList.add(WeeklyMentalConditionResponse.of("???", 0));
        weeklyMentalConditionResponseList.add(WeeklyMentalConditionResponse.of("???", 0));
        weeklyMentalConditionResponseList.add(WeeklyMentalConditionResponse.of("???", 3));
        weeklyMentalConditionResponseList.add(WeeklyMentalConditionResponse.of("???", 2));
        weeklyMentalConditionResponseList.add(WeeklyMentalConditionResponse.of("??????", 5));
        return weeklyMentalConditionResponseList;
    }

    private List<FeelingRankResponse> mockFeelingRankResponse() {
        List<FeelingRankResponse> feelingRankResponseList = new ArrayList<>();
        feelingRankResponseList.add(
                FeelingRankResponse.builder()
                        .feeling("?????????")
                        .count(5)
                        .build());
        feelingRankResponseList.add(
                FeelingRankResponse.builder()
                        .feeling("?????????")
                        .count(3)
                        .build());
        feelingRankResponseList.add(
                FeelingRankResponse.builder()
                        .feeling("???????????????")
                        .count(1)
                        .build());
        return feelingRankResponseList;
    }

}