package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.dto.response.MydayResponse;
import great.job.mytamin.domain.user.dto.response.ProfileResponse;
import great.job.mytamin.domain.user.service.MypageService;
import great.job.mytamin.global.support.CommonControllerTest;
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

@WebMvcTest(controllers = MypageController.class)
@DisplayName("Mypage 컨트롤러")
class MypageControllerTest extends CommonControllerTest {

    @MockBean
    private MypageService mypageService;

    @DisplayName("마이페이지 프로필 조회")
    @Test
    void getProfile(TestInfo testInfo) throws Exception {
        //given
        given(mypageService.getProfile(any())).willReturn(ProfileResponse.of(user));

        // when
        ResultActions actions = mockMvc.perform(get("/mypage/profile")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}"));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/mypage/" + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data.nickname").description("닉네임"),
                                fieldWithPath("data.profileImgUrl").description("프로필 이미지 URL"),
                                fieldWithPath("data.beMyMessage").description("'되고싶은 내 모습' 메세지")
                        ))
                );
    }

    @DisplayName("이번 달의 마이데이")
    @Test
    void getMyday(TestInfo testInfo) throws Exception {
        //given
        given(mypageService.getMyday(any())).willReturn(
                MydayResponse.builder()
                        .myDayMMDD("10월 17일")
                        .dday("D-7일")
                        .msg("이번 마이데이에는 무엇을 해볼까요 ?")
                        .build()
        );

        // when
        ResultActions actions = mockMvc.perform(get("/mypage/myday")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}"));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/mypage/" + testInfo.getTestMethod().get().getName(),
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