package great.job.mytamin.domain.home.controller;

import great.job.mytamin.domain.home.dto.response.ActiveResponse;
import great.job.mytamin.domain.home.dto.response.WelcomeResponse;
import great.job.mytamin.domain.home.service.HomeService;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
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

@WebMvcTest(controllers = HomeController.class)
@DisplayName("Home 컨트롤러")
class HomeControllerTest extends CommonControllerTest {

    @MockBean
    private HomeService homeService;

    @DisplayName("웰컴 메세지")
    @Test
    void welcome(TestInfo testInfo) throws Exception {
        //given
        given(homeService.welcome(any())).willReturn(
                WelcomeResponse.builder()
                        .nickname(user.getNickname())
                        .comment("어떤 하루를 보내고 계신가요 ?")
                        .build()
        );

        //when
        ResultActions actions = mockMvc.perform(get("/home/welcome")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}"));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/home/" + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data.nickname").description("닉네임"),
                                fieldWithPath("data.comment").description("웰컴 메세지")
                        ))
                );
    }

    @DisplayName("행동 완료 상태")
    @Test
    void getProgressStatus(TestInfo testInfo) throws Exception {
        //given
        given(homeService.getProgressStatus(any())).willReturn(
                ActiveResponse.builder()
                        .breathIsDone(true)
                        .senseIsDone(true)
                        .reportIsDone(false)
                        .careIsDone(false)
                        .build()
        );

        //when
        ResultActions actions = mockMvc.perform(get("/home/progress/status")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}"));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/home/" + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data.breathIsDone").description("숨 고르기 행동 완료 여부"),
                                fieldWithPath("data.senseIsDone").description("감각 깨우기 행동 완료 여부"),
                                fieldWithPath("data.reportIsDone").description("하루 진단 행동 완료 여부"),
                                fieldWithPath("data.careIsDone").description("칭찬 처방 행동 완료 여부")
                        ))
                );
    }

    @DisplayName("숨 고르기 완료")
    @Test
    void completeBreath(TestInfo testInfo) throws Exception {
        //given
        doNothing().when(homeService).completeBreath(any());

        // when
        ResultActions actions = mockMvc.perform(patch("/home/breath")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}"));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("/home/" + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data").ignored()
                        ))
                );
    }

    @DisplayName("감각 깨우기 완료")
    @Test
    void completeSense(TestInfo testInfo) throws Exception {
        //given
        doNothing().when(homeService).completeSense(any());

        // when
        ResultActions actions = mockMvc.perform(patch("/home/sense")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}"));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("/home/" + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data").ignored()
                        ))
                );
    }

}