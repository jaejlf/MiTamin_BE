package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.dto.response.CareResponse;
import great.job.mytamin.domain.mytamin.dto.response.MytaminResponse;
import great.job.mytamin.domain.mytamin.service.MytaminService;
import great.job.mytamin.domain.mytamin.dto.response.ReportResponse;
import great.job.mytamin.domain.mytamin.enumerate.MentalCondition;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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

}