package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.dto.response.CareResponse;
import great.job.mytamin.domain.mytamin.dto.response.MonthlyMytaminResponse;
import great.job.mytamin.domain.mytamin.dto.response.MytaminResponse;
import great.job.mytamin.domain.mytamin.dto.response.ReportResponse;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

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
@DisplayName("Mytamin 컨트롤러")
class MytaminControllerTest extends CommonControllerTest {

    String docId = "/mytamin/";

    @MockBean
    private MytaminService mytaminService;

    @DisplayName("숨 고르기 완료")
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
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지")
                        ))
                );
    }

    @DisplayName("감각 깨우기 완료")
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
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지")
                        ))
                );
    }

    @DisplayName("최근 섭취한 마이타민")
    @Test
    void getLatestMytamin(TestInfo testInfo) throws Exception {
        //given
        ReportResponse report = ReportResponse.builder()
                .reportId(1L)
                .canEdit(true)
                .mentalConditionCode(5)
                .mentalCondition(MentalCondition.VERY_GOOD.getMsg())
                .feelingTag("#신나는 #즐거운 #재밌는")
                .todayReport("아무래도 아침형 인간이 되는건 너무 어려운 것 같다.")
                .build();

        CareResponse care = CareResponse.builder()
                .careId(1L)
                .canEdit(true)
                .careCategory("이루어 낸 일")
                .careMsg1("오늘 할 일을 전부 했어")
                .careMsg2("성실히 노력하는 내 모습이 좋아")
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
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data.takeAt").description("마이타민 섭취 날짜"),
                                // report
                                fieldWithPath("data.report.reportId").description("하루 진단 id"),
                                fieldWithPath("data.report.canEdit").description("'하루 진단' 수정 가능 여부"),
                                fieldWithPath("data.report.mentalConditionCode").description("마음 컨디션 코드"),
                                fieldWithPath("data.report.mentalCondition").description("마음 컨디션 메세지"),
                                fieldWithPath("data.report.feelingTag").description("감정 태그"),
                                fieldWithPath("data.report.todayReport").description("하루 진단"),
                                // care
                                fieldWithPath("data.care.careId").description("칭찬 처방 id"),
                                fieldWithPath("data.care.canEdit").description("'칭찬 처방' 수정 가능 여부"),
                                fieldWithPath("data.care.careCategory").description("칭찬 카테고리"),
                                fieldWithPath("data.care.careMsg1").description("칭찬 처방 메세지 1"),
                                fieldWithPath("data.care.careMsg2").description("칭찬 처방 메세지 2")
                        ))
                );
    }

    @DisplayName("월간 마이타민 기록 조회")
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
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("date").description("*조회할 날짜 (yyyy.MM)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data[].day").description("날짜"),
                                fieldWithPath("data[].mentalConditionCode").description("마음 컨디션 코드")
                        ))
                );
    }

    @DisplayName("주간 마이타민 기록 조회")
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
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        pathParameters(
                                parameterWithName("date").description("*조회할 날짜 (yyyy.MM.dd)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data[].mytaminId").description("마이타민 id"),
                                fieldWithPath("data[].takeAt").description("마이타민 섭취 날짜"),
                                // report
                                fieldWithPath("data[].report").description("하루 진단").optional().ignored(),
                                fieldWithPath("data[].report.reportId").description("하루 진단 id"),
                                fieldWithPath("data[].report.canEdit").description("'하루 진단' 수정 가능 여부"),
                                fieldWithPath("data[].report.mentalConditionCode").description("마음 컨디션 코드"),
                                fieldWithPath("data[].report.mentalCondition").description("마음 컨디션 메세지"),
                                fieldWithPath("data[].report.feelingTag").description("감정 태그"),
                                fieldWithPath("data[].report.todayReport").description("하루 진단"),
                                // care
                                fieldWithPath("data[].care").description("칭찬 처방").optional().ignored(),
                                fieldWithPath("data[].care.careId").description("칭찬 처방 id"),
                                fieldWithPath("data[].care.canEdit").description("'칭찬 처방' 수정 가능 여부"),
                                fieldWithPath("data[].care.careCategory").description("칭찬 카테고리"),
                                fieldWithPath("data[].care.careMsg1").description("칭찬 처방 메세지 1"),
                                fieldWithPath("data[].care.careMsg2").description("칭찬 처방 메세지 2")
                        ))
                );
    }

    @Nested
    @DisplayName("마이타민 삭제")
    class DeleteMytaminTest {

        Long mytaminId = 1L;

        @DisplayName("성공")
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
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("mytaminId").description("*삭제할 마이타민 id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지")
                            ))
                    );
        }

        @DisplayName("존재하지 않는 마이타민 id")
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
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

    private List<MytaminResponse> mockMytaminList() {
        List<MytaminResponse> mytaminResponseList = new ArrayList<>();
        mytaminResponseList.add(MytaminResponse.builder().mytaminId(1L).takeAt("10.17.Mon")
                .report(null)
                .care(CareResponse.builder().careId(1L).canEdit(false).careCategory("이루어 낸 일").careMsg1("오늘 할 일을 전부 했어").careMsg2("성실히 노력하는 내 모습이 좋아").build())
                .build());
        mytaminResponseList.add(MytaminResponse.builder().mytaminId(2L).takeAt("10.18.Tue")
                .report(ReportResponse.builder().reportId(1L).canEdit(false).mentalConditionCode(5).mentalCondition(MentalCondition.VERY_GOOD.getMsg()).feelingTag("#신나는").todayReport("아무래도 아침형 인간이 되는건 너무 어려운 것 같다.").build())
                .care(null)
                .build());
        mytaminResponseList.add(MytaminResponse.builder().mytaminId(3L).takeAt("10.19.Wed")
                .report(ReportResponse.builder().reportId(2L).canEdit(false).mentalConditionCode(4).mentalCondition(MentalCondition.GOOD.getMsg()).feelingTag("#즐거운 #행복한").todayReport("즐겁고 행복한 수요일").build())
                .care(CareResponse.builder().careId(2L).canEdit(false).careCategory("이루어 낸 일").careMsg1("늦잠자고 싶었지만 그러지 않았어").careMsg2("그래서 여유롭게 커피를 먹을 수 있었어").build())
                .build());
        mytaminResponseList.add(MytaminResponse.builder().mytaminId(4L).takeAt("10.20.Thu")
                .report(null)
                .care(CareResponse.builder().careId(3L).canEdit(false).careCategory("잘한 일이나 행동").careMsg1("사고 싶은게 있었지만 잘 참았어").careMsg2("충동구매를 하지 않는 모습.. 멋져 !").build())
                .build());
        mytaminResponseList.add(MytaminResponse.builder().mytaminId(5L).takeAt("10.21.Fri")
                .report(ReportResponse.builder().reportId(3L).canEdit(false).mentalConditionCode(3).mentalCondition(MentalCondition.SO_SO.getMsg()).feelingTag("#평온한 #지루한 #권태로운").todayReport("별거 없었던 오늘의 하루").build())
                .care(null)
                .build());
        mytaminResponseList.add(MytaminResponse.builder().mytaminId(6L).takeAt("10.22.Sat")
                .report(null)
                .care(CareResponse.builder().careId(4L).canEdit(false).careCategory("노력하고 있는 부분").careMsg1("너무 늦게 잠들지 않기 위해 노력 중이야").careMsg2("그치만 오늘도 해가 뜰 때쯤 잠들었어").build())
                .build());
        mytaminResponseList.add(MytaminResponse.builder().mytaminId(7L).takeAt("10.23.Sun")
                .report(ReportResponse.builder().reportId(4L).canEdit(false).mentalConditionCode(5).mentalCondition(MentalCondition.VERY_GOOD.getMsg()).feelingTag("#신나는").todayReport("해압삐한 일요일이다 !").build())
                .care(null)
                .build());

        return mytaminResponseList;
    }

}