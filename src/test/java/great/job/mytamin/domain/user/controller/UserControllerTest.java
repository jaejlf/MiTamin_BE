package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.dto.request.BeMyMsgRequest;
import great.job.mytamin.domain.user.service.UserService;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static great.job.mytamin.global.exception.ErrorMap.NICKNAME_DUPLICATE_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@DisplayName("User 컨트롤러")
class UserControllerTest extends CommonControllerTest {

    @MockBean
    private UserService userService;

    @Nested
    @DisplayName("닉네임 수정")
    class UpdateNicknameTest {

        @DisplayName("성공")
        @Test
        void updateNickname(TestInfo testInfo) throws Exception {
            //given
            String nickname = "멘탈짱";

            doNothing().when(userService).updateNickname(any(), any());

            // when
            ResultActions actions = mockMvc.perform(patch("/user/nickname/{nickname}", nickname)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}"));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document("/user/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("nickname").description("*수정할 닉네임 (1 ~ 9자)")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data").ignored()
                            ))
                    );
        }

        @DisplayName("이미 사용 중인 닉네임")
        @Test
        void updateNickname_2003(TestInfo testInfo) throws Exception {
            //given
            String nickname = "멘탈짱";

            doThrow(new MytaminException(NICKNAME_DUPLICATE_ERROR)).when(userService).updateNickname(any(), any());

            // when
            ResultActions actions = mockMvc.perform(patch("/user/nickname/{nickname}", nickname)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}"));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("errorCode").value(2003))
                    .andExpect(jsonPath("errorName").value("NICKNAME_DUPLICATE_ERROR"))
                    .andDo(document("/user/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("nickname").description("*수정할 닉네임")
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

    @DisplayName("'되고 싶은 나' 메세지 수정")
    @Test
    void updateBeMyMessage(TestInfo testInfo) throws Exception {
        //given
        BeMyMsgRequest beMyMsgRequest = new BeMyMsgRequest(
                "꾸준히 글을 쓰는"
        );

        doNothing().when(userService).updateNickname(any(), any());

        // when
        ResultActions actions = mockMvc.perform(patch("/user/bemy-msg")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .content(objectMapper.writeValueAsString(beMyMsgRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("/user/" + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        requestFields(
                                fieldWithPath("beMyMessage").description("*수정할 '되고 싶은 나' 메세지 (1 ~ 20자)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data").ignored()
                        ))
                );
    }

}