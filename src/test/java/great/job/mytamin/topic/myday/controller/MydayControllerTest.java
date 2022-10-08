package great.job.mytamin.topic.myday.controller;

import great.job.mytamin.global.support.CommonControllerTest;
import great.job.mytamin.topic.myday.dto.response.MydayResponse;
import great.job.mytamin.topic.myday.service.MydayService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = MydayController.class)
@DisplayName("Myday 컨트롤러")
class MydayControllerTest extends CommonControllerTest {

    String docId = "/myday/";

    @MockBean
    private MydayService mydayService;

    @DisplayName("이번 달의 마이데이")
    @Test
    void getMyday(TestInfo testInfo) throws Exception {
        //given
        given(mydayService.getMyday(any())).willReturn(
                MydayResponse.builder()
                        .myDayMMDD("10월 17일")
                        .dday("D-7일")
                        .msg("이번 마이데이에는 무엇을 해볼까요 ?")
                        .build()
        );

        // when
        ResultActions actions = mockMvc.perform(get("/myday/info")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}"));

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
                                fieldWithPath("data.myDayMMDD").description("마이데이 날짜"),
                                fieldWithPath("data.dday").description("현재 시간 ~ 마이데이 날짜 D-Day"),
                                fieldWithPath("data.msg").description("마이데이 메세지")
                        ))
                );
    }

}