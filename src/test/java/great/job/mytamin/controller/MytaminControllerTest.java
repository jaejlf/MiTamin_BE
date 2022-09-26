package great.job.mytamin.controller;

import great.job.mytamin.domain.Care;
import great.job.mytamin.domain.Mytamin;
import great.job.mytamin.domain.Report;
import great.job.mytamin.dto.request.CareRequest;
import great.job.mytamin.dto.request.ReportRequest;
import great.job.mytamin.dto.response.CareResponse;
import great.job.mytamin.dto.response.ReportResponse;
import great.job.mytamin.enumerate.CareCategory;
import great.job.mytamin.enumerate.MentalCondition;
import great.job.mytamin.exception.MytaminException;
import great.job.mytamin.service.MytaminService;
import great.job.mytamin.support.CommonControllerTest;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import static great.job.mytamin.exception.ErrorMap.*;
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

@WebMvcTest(controllers = MytaminController.class)
@DisplayName("Mytamin 컨트롤러")
class MytaminControllerTest extends CommonControllerTest {

    @MockBean
    private MytaminService mytaminService;

    private Mytamin mytamin;

    @BeforeEach
    void setUp() {
        LocalDateTime rawTakeAt = LocalDateTime.now();
        String dayOfWeek = rawTakeAt.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        String takeAt = rawTakeAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "." + dayOfWeek;
        mytamin = new Mytamin(
                rawTakeAt,
                takeAt,
                user
        );
    }

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
                    "아무래도 아침형 인간이 되는건 너무 어려운 것 같다."
            );
            given(mytaminService.reportToday(any(), any())).willReturn(ReportResponse.of(
                    new Report(
                            MentalCondition.VERY_GOOD.getMsg(),
                            "신나는",
                            "아무래도 아침형 인간이 되는건 너무 어려운 것 같다.",
                            mytamin
                    )
            ));

            //when
            ResultActions actions = mockMvc.perform(post("/mytamin/report")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(reportRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document("/mytamin/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("mentalConditionCode").description("*마음 컨디션 코드"),
                                    fieldWithPath("feelingTag").description("*감정 태그"),
                                    fieldWithPath("todayReport").description("*하루 진단")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("상태 코드"),
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
        void reportToday_REPORT_ALREADY_DONE(TestInfo testInfo) throws Exception {
            //given
            ReportRequest reportRequest = new ReportRequest(
                    5,
                    "신나는",
                    "아무래도 아침형 인간이 되는건 너무 어려운 것 같다."
            );
            given(mytaminService.reportToday(any(), any())).willThrow(new MytaminException(REPORT_ALREADY_DONE));

            //when
            ResultActions actions = mockMvc.perform(post("/mytamin/report")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(reportRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("errorName").value("REPORT_ALREADY_DONE"))
                    .andDo(document("/mytamin/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("mentalConditionCode").description("*마음 컨디션 코드"),
                                    fieldWithPath("feelingTag").description("*감정 태그"),
                                    fieldWithPath("todayReport").description("*하루 진단")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("상태 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("마음 컨디션 코드 오류")
        @Test
        void reportToday_INVALID_CONDITION_CODE_ERROR(TestInfo testInfo) throws Exception {
            //given
            ReportRequest reportRequest = new ReportRequest(
                    5,
                    "신나는",
                    "아무래도 아침형 인간이 되는건 너무 어려운 것 같다."
            );
            given(mytaminService.reportToday(any(), any())).willThrow(new MytaminException(INVALID_CONDITION_CODE_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/mytamin/report")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(reportRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorName").value("INVALID_CONDITION_CODE_ERROR"))
                    .andDo(document("/mytamin/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("mentalConditionCode").description("*마음 컨디션 코드"),
                                    fieldWithPath("feelingTag").description("*감정 태그"),
                                    fieldWithPath("todayReport").description("*하루 진단")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("상태 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("칭찬 처방하기")
    class CareTodayTest {

        @DisplayName("성공")
        @Test
        void careToday(TestInfo testInfo) throws Exception {
            //given
            CareRequest careRequest = new CareRequest(
                    1,
                    "오늘 할 일을 전부 했어",
                    "성실히 노력하는 내 모습이 좋아"
            );
            given(mytaminService.careToday(any(), any())).willReturn(CareResponse.of(
                    new Care(
                            CareCategory.ACCOMPLISHED.getMsg(),
                            "오늘 할 일을 전부 했어",
                            "성실히 노력하는 내 모습이 좋아",
                            mytamin
                    )
            ));

            //when
            ResultActions actions = mockMvc.perform(post("/mytamin/care")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(careRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document("/mytamin/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("careCategoryCode").description("*칭찬 카테고리 코드"),
                                    fieldWithPath("careMsg1").description("*칭찬 처방 메세지 1"),
                                    fieldWithPath("careMsg2").description("*칭찬 처방 메세지 2")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.takeAt").description("마이타민 섭취 날짜"),
                                    fieldWithPath("data.careCategory").description("칭찬 카테고리"),
                                    fieldWithPath("data.careMsg1").description("칭찬 처방 메세지 1"),
                                    fieldWithPath("data.careMsg2").description("칭찬 처방 메세지 2")
                            ))
                    );
        }

        @DisplayName("이미 칭찬 처방 완료")
        @Test
        void careToday_CARE_ALREADY_DONE(TestInfo testInfo) throws Exception {
            //given
            CareRequest careRequest = new CareRequest(
                    1,
                    "오늘 할 일을 전부 했어",
                    "성실히 노력하는 내 모습이 좋아"
            );
            given(mytaminService.careToday(any(), any())).willThrow(new MytaminException(CARE_ALREADY_DONE));

            //when
            ResultActions actions = mockMvc.perform(post("/mytamin/care")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(careRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("errorName").value("CARE_ALREADY_DONE"))
                    .andDo(document("/mytamin/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("careCategoryCode").description("*칭찬 카테고리 코드"),
                                    fieldWithPath("careMsg1").description("*칭찬 처방 메세지 1"),
                                    fieldWithPath("careMsg2").description("*칭찬 처방 메세지 2")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("상태 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("칭찬 카테고리 코드 오류")
        @Test
        void careToday_INVALID_CATEGORY_CODE_ERROR(TestInfo testInfo) throws Exception {
            //given
            CareRequest careRequest = new CareRequest(
                    1,
                    "오늘 할 일을 전부 했어",
                    "성실히 노력하는 내 모습이 좋아"
            );
            given(mytaminService.careToday(any(), any())).willThrow(new MytaminException(INVALID_CATEGORY_CODE_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/mytamin/care")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(careRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorName").value("INVALID_CATEGORY_CODE_ERROR"))
                    .andDo(document("/mytamin/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("careCategoryCode").description("*칭찬 카테고리 코드"),
                                    fieldWithPath("careMsg1").description("*칭찬 처방 메세지 1"),
                                    fieldWithPath("careMsg2").description("*칭찬 처방 메세지 2")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("상태 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

}