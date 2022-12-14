package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.dto.response.*;
import great.job.mytamin.domain.mytamin.enumerate.MentalCondition;
import great.job.mytamin.domain.mytamin.service.MytaminService;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.*;

import static great.job.mytamin.global.exception.ErrorMap.MYTAMIN_NOT_FOUND_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MytaminController.class)
@DisplayName("Mytamin ????????????")
class MytaminControllerTest extends CommonControllerTest {

    String docId = "/mytamin/";

    @MockBean
    private MytaminService mytaminService;

    @DisplayName("??? ????????? ??????")
    @Test
    void completeBreath(TestInfo testInfo) throws Exception {
        //given
        doNothing().when(mytaminService).completeBreath(any());

        // when
        ResultActions actions = mockMvc.perform(patch("/mytamin/breath")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????")
                        ))
                );
    }

    @DisplayName("?????? ????????? ??????")
    @Test
    void completeSense(TestInfo testInfo) throws Exception {
        //given
        doNothing().when(mytaminService).completeSense(any());

        // when
        ResultActions actions = mockMvc.perform(patch("/mytamin/sense")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????")
                        ))
                );
    }

    @DisplayName("?????? ????????? ????????????")
    @Test
    void getLatestMytamin(TestInfo testInfo) throws Exception {
        //given
        ReportResponse report = ReportResponse.builder()
                .reportId(1L)
                .canEdit(true)
                .mentalConditionCode(5)
                .mentalCondition(MentalCondition.VERY_GOOD.getMsg())
                .feelingTag("#????????? #????????? #?????????")
                .todayReport("???????????? ????????? ????????? ????????? ?????? ????????? ??? ??????.")
                .build();

        CareResponse care = CareResponse.builder()
                .careId(1L)
                .canEdit(true)
                .careCategory("????????? ??? ???")
                .careMsg1("?????? ??? ?????? ?????? ??????")
                .careMsg2("????????? ???????????? ??? ????????? ??????")
                .build();

        LocalDateTime target = mytamin.getTakeAt();
        String dayOfWeek = target.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        given(mytaminService.getLatestMytamin(any())).willReturn(MytaminResponse.builder()
                .takeAt(target.format(DateTimeFormatter.ofPattern("MM.dd")) + "." + dayOfWeek)
                .report(report)
                .care(care)
                .build()
        );

        //when
        ResultActions actions = mockMvc.perform(get("/mytamin/latest")
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
                                fieldWithPath("data.takeAt").description("???????????? ?????? ??????"),
                                // report
                                fieldWithPath("data.report.reportId").description("?????? ?????? id"),
                                fieldWithPath("data.report.canEdit").description("'?????? ??????' ?????? ?????? ??????"),
                                fieldWithPath("data.report.mentalConditionCode").description("?????? ????????? ??????"),
                                fieldWithPath("data.report.mentalCondition").description("?????? ????????? ?????????"),
                                fieldWithPath("data.report.feelingTag").description("?????? ??????"),
                                fieldWithPath("data.report.todayReport").description("?????? ??????"),
                                // care
                                fieldWithPath("data.care.careId").description("?????? ?????? id"),
                                fieldWithPath("data.care.canEdit").description("'?????? ??????' ?????? ?????? ??????"),
                                fieldWithPath("data.care.careCategory").description("?????? ????????????"),
                                fieldWithPath("data.care.careMsg1").description("?????? ?????? ????????? 1"),
                                fieldWithPath("data.care.careMsg2").description("?????? ?????? ????????? 2")
                        ))
                );
    }

    @DisplayName("?????? ???????????? ?????? ??????")
    @Test
    void getMonthlyMytamin(TestInfo testInfo) throws Exception {
        //given
        String date = "2022.10";

        given(mytaminService.getMonthlyMytamin(any(), any())).willReturn(mockMonthlyMytaminResponse());

        //when
        ResultActions actions = mockMvc.perform(get("/mytamin/monthly/{date}", date)
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
                                parameterWithName("date").description("*????????? ?????? (yyyy.MM)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data[].day").description("??????"),
                                fieldWithPath("data[].mentalConditionCode").description("?????? ????????? ??????")
                        ))
                );
    }

    @DisplayName("?????? ???????????? ?????? ??????")
    @Test
    void getWeeklyMytamin(TestInfo testInfo) throws Exception {
        //given
        String date = "2022.10.19";

        given(mytaminService.getWeeklyMytamin(any(), any())).willReturn(mockMytaminList());

        //when
        ResultActions actions = mockMvc.perform(get("/mytamin/weekly/{date}", date)
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
                                parameterWithName("date").description("*????????? ?????? (yyyy.MM.dd)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data.*").description("??????"),
                                fieldWithPath("data.*.mytaminId").description("???????????? id"),
                                fieldWithPath("data.*.takeAt").description("???????????? ?????? ??????"),
                                // report
                                fieldWithPath("data.*.report").description("?????? ??????").optional().ignored(),
                                fieldWithPath("data.*.report.reportId").description("?????? ?????? id"),
                                fieldWithPath("data.*.report.canEdit").description("'?????? ??????' ?????? ?????? ??????"),
                                fieldWithPath("data.*.report.mentalConditionCode").description("?????? ????????? ??????"),
                                fieldWithPath("data.*.report.mentalCondition").description("?????? ????????? ?????????"),
                                fieldWithPath("data.*.report.feelingTag").description("?????? ??????"),
                                fieldWithPath("data.*.report.todayReport").description("?????? ??????"),
                                // care
                                fieldWithPath("data.*.care").description("?????? ??????").optional().ignored(),
                                fieldWithPath("data.*.care.careId").description("?????? ?????? id"),
                                fieldWithPath("data.*.care.canEdit").description("'?????? ??????' ?????? ?????? ??????"),
                                fieldWithPath("data.*.care.careCategory").description("?????? ????????????"),
                                fieldWithPath("data.*.care.careMsg1").description("?????? ?????? ????????? 1"),
                                fieldWithPath("data.*.care.careMsg2").description("?????? ?????? ????????? 2")
                        ))
                );
    }

    @Nested
    @DisplayName("???????????? ??????")
    class DeleteMytaminTest {

        Long mytaminId = 1L;

        @DisplayName("??????")
        @Test
        void deleteMytamin(TestInfo testInfo) throws Exception {
            //given
            doNothing().when(mytaminService).deleteMytamin(any(), any());

            //when
            ResultActions actions = mockMvc.perform(delete("/mytamin/{mytaminId}", mytaminId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
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
                                    parameterWithName("mytaminId").description("*????????? ???????????? id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ???????????? id")
        @Test
        void deleteMytamin_5003(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(MYTAMIN_NOT_FOUND_ERROR)).when(mytaminService).deleteMytamin(any(), any());

            //when
            ResultActions actions = mockMvc.perform(delete("/mytamin/{mytaminId}", mytaminId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(5003))
                    .andExpect(jsonPath("errorName").value("MYTAMIN_NOT_FOUND_ERROR"))
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

    private List<MonthlyMytaminResponse> mockMonthlyMytaminResponse() {
        List<MonthlyMytaminResponse> monthlyMytaminResponseList = new ArrayList<>();
        monthlyMytaminResponseList.add(MonthlyMytaminResponse.builder().day(19).mentalConditionCode(1).build());
        monthlyMytaminResponseList.add(MonthlyMytaminResponse.builder().day(20).mentalConditionCode(5).build());
        monthlyMytaminResponseList.add(MonthlyMytaminResponse.builder().day(21).mentalConditionCode(3).build());
        monthlyMytaminResponseList.add(MonthlyMytaminResponse.builder().day(22).mentalConditionCode(9).build());
        monthlyMytaminResponseList.add(MonthlyMytaminResponse.builder().day(23).mentalConditionCode(9).build());
        return monthlyMytaminResponseList;
    }

    private Map<Integer, WeeklyMytaminResponse> mockMytaminList() {
        Map<Integer, WeeklyMytaminResponse> map = new LinkedHashMap<>();
        map.put(17, WeeklyMytaminResponse.builder()
                .mytaminId(1L)
                .takeAt("10??? 17?????? ????????????")
                .report(ReportResponse.builder().reportId(1L).canEdit(false).mentalConditionCode(5).mentalCondition(MentalCondition.VERY_GOOD.getMsg()).feelingTag("#?????????").todayReport("???????????? ????????? ????????? ????????? ?????? ????????? ??? ??????.").build())
                .care(null)
                .build());
        map.put(18, WeeklyMytaminResponse.builder()
                .mytaminId(2L)
                .takeAt("10??? 18?????? ????????????")
                .report(null)
                .care(CareResponse.builder().careId(1L).canEdit(false).careCategory("????????? ??? ???").careMsg1("?????? ??? ?????? ?????? ??????").careMsg2("????????? ???????????? ??? ????????? ??????").build())
                .build());
        return map;
    }

}