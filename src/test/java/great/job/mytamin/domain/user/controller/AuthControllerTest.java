package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.dto.request.EmailCheckRequest;
import great.job.mytamin.domain.user.dto.request.LoginRequest;
import great.job.mytamin.domain.user.dto.request.ReissueRequest;
import great.job.mytamin.domain.user.dto.request.SignUpRequest;
import great.job.mytamin.domain.user.dto.response.TokenResponse;
import great.job.mytamin.domain.user.dto.response.UserResponse;
import great.job.mytamin.domain.user.service.AuthService;
import great.job.mytamin.domain.user.service.EmailService;
import great.job.mytamin.domain.util.UserUtil;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.HashMap;
import java.util.Map;

import static great.job.mytamin.global.exception.ErrorMap.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@DisplayName("Auth ????????????")
class AuthControllerTest extends CommonControllerTest {

    String docId = "/auth/";

    @MockBean
    private AuthService authService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private UserUtil userUtil;

    @Nested
    @DisplayName("?????? ??????")
    class SignUpTest {

        SignUpRequest signUpRequest = new SignUpRequest(
                "mytamin@naver.com",
                "password1234",
                "????????????"
        );

        @DisplayName("??????")
        @Test
        void signUp(TestInfo testInfo) throws Exception {
            //given
            given(authService.signUp(any())).willReturn(UserResponse.of(user));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/signup")
                    .content(objectMapper.writeValueAsString(signUpRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*?????????"),
                                    fieldWithPath("password").description("*???????????? (8 ~ 30???)"),
                                    fieldWithPath("nickname").description("*????????? (1 ~ 9???)")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????"),
                                    fieldWithPath("data.email").description("?????????"),
                                    fieldWithPath("data.nickname").description("?????????"),
                                    fieldWithPath("data.profileImgUrl").description("????????? ????????? URL (default : null)"),
                                    fieldWithPath("data.beMyMessage").description("'???????????? ??? ??????' ????????? (default : \"?????? ???????????? ?????????\")"),
                                    fieldWithPath("data.mytaminHour").description("???????????? ?????? ?????? ?????? HH (24??????) (default : null)"),
                                    fieldWithPath("data.mytaminMin").description("???????????? ?????? ?????? ?????? MM (default : null)")
                            ))
                    );
        }

        @DisplayName("????????? ?????? ??????")
        @Test
        void signUp_2000(TestInfo testInfo) throws Exception {
            //given
            given(authService.signUp(any())).willThrow(new MytaminException(EMAIL_PATTERN_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/signup")
                    .content(objectMapper.writeValueAsString(signUpRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(2000))
                    .andExpect(jsonPath("errorName").value("EMAIL_PATTERN_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ??????")
        @Test
        void signUp_2001(TestInfo testInfo) throws Exception {
            //given
            given(authService.signUp(any())).willThrow(new MytaminException(PASSWORD_PATTERN_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/signup")
                    .content(objectMapper.writeValueAsString(signUpRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(2001))
                    .andExpect(jsonPath("errorName").value("PASSWORD_PATTERN_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("?????? ????????? ??????")
        @Test
        void signUp_2002(TestInfo testInfo) throws Exception {
            //given
            given(authService.signUp(any())).willThrow(new MytaminException(USER_ALREADY_EXIST_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/signup")
                    .content(objectMapper.writeValueAsString(signUpRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("errorCode").value(2002))
                    .andExpect(jsonPath("errorName").value("USER_ALREADY_EXIST_ERROR"))
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
    @DisplayName("?????? ?????????")
    class DefaultLoginTest {

        LoginRequest loginRequest = new LoginRequest(
                "mytamin@naver.com",
                "password1234"
        );

        @DisplayName("??????")
        @Test
        void defaultLogin(TestInfo testInfo) throws Exception {
            //given
            given(authService.defaultLogin(any())).willReturn(
                    TokenResponse.builder()
                            .accessToken("{{ACCESS_TOKEN}}")
                            .refreshToken("{{REFRESH_TOKEN}}")
                            .build()
            );

            //when
            ResultActions actions = mockMvc.perform(post("/auth/default/login")
                    .content(objectMapper.writeValueAsString(loginRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*?????????"),
                                    fieldWithPath("password").description("*???????????? (8 ~ 30???)")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????"),
                                    fieldWithPath("data.accessToken").description("????????? ?????? (?????? ?????? : 30???)"),
                                    fieldWithPath("data.refreshToken").description("???????????? ?????? (?????? ?????? : 180???)")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ??????")
        @Test
        void defaultLogin_3000(TestInfo testInfo) throws Exception {
            //given
            given(authService.defaultLogin(any())).willThrow(new MytaminException(USER_NOT_FOUND_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/default/login")
                    .content(objectMapper.writeValueAsString(loginRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(3000))
                    .andExpect(jsonPath("errorName").value("USER_NOT_FOUND_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("????????? ????????????")
        @Test
        void defaultLogin_3001(TestInfo testInfo) throws Exception {
            //given
            given(authService.defaultLogin(any())).willThrow(new MytaminException(PASSWORD_MISMATCH_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/default/login")
                    .content(objectMapper.writeValueAsString(loginRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(3001))
                    .andExpect(jsonPath("errorName").value("PASSWORD_MISMATCH_ERROR"))
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

    @DisplayName("????????? ?????? ??????")
    @Test
    void isEmailDuplicate(TestInfo testInfo) throws Exception {
        //given
        String email = "mytamin@naver.com";
        given(userUtil.isEmailDuplicate(any())).willReturn(true);

        //when
        ResultActions actions = mockMvc.perform(get("/auth/check/email/{email}", email)
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        pathParameters(
                                parameterWithName("email").description("*?????????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data").description("????????? ?????? ??????")
                        ))
                );
    }

    @DisplayName("????????? ?????? ??????")
    @Test
    void isNicknameDuplicate(TestInfo testInfo) throws Exception {
        //given
        String nickname = "mental-zzang";
        given(userUtil.isNicknameDuplicate(any())).willReturn(true);

        //when
        ResultActions actions = mockMvc.perform(get("/auth/check/nickname/{nickname}", nickname)
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        pathParameters(
                                parameterWithName("nickname").description("*?????????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data").description("????????? ?????? ??????")
                        ))
                );
    }

    @Nested
    @DisplayName("?????? ?????????")
    class ReissueTest {

        ReissueRequest tokenRequest = new ReissueRequest(
                "mytamin@naver.com",
                "{{REFRESH_TOKEN}}"
        );

        @DisplayName("??????")
        @Test
        void reissueToken(TestInfo testInfo) throws Exception {
            //given
            given(authService.reissueToken(any())).willReturn(
                    TokenResponse.builder()
                            .accessToken("{{ACCESS_TOKEN}}")
                            .refreshToken("{{REFRESH_TOKEN}}")
                            .build()
            );

            //when
            ResultActions actions = mockMvc.perform(post("/auth/reissue")
                    .content(objectMapper.writeValueAsString(tokenRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*?????????"),
                                    fieldWithPath("refreshToken").description("*???????????? ??????")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????"),
                                    fieldWithPath("data.accessToken").description("????????? ?????? (?????? ?????? : 30???)"),
                                    fieldWithPath("data.refreshToken").description("???????????? ?????? (?????? ?????? : 180???)")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ??????")
        @Test
        void reissueToken_3000(TestInfo testInfo) throws Exception {
            //given
            given(authService.reissueToken(any())).willThrow(new MytaminException(USER_NOT_FOUND_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/reissue")
                    .content(objectMapper.writeValueAsString(tokenRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(3000))
                    .andExpect(jsonPath("errorName").value("USER_NOT_FOUND_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("DB??? ????????? ????????? ?????????")
        @Test
        void reissueToken_1001(TestInfo testInfo) throws Exception {
            //given
            given(authService.reissueToken(any())).willThrow(new MytaminException(INVALID_TOKEN_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/reissue")
                    .content(objectMapper.writeValueAsString(tokenRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isForbidden())
                    .andExpect(jsonPath("errorCode").value(1001))
                    .andExpect(jsonPath("errorName").value("INVALID_TOKEN_ERROR"))
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

    @DisplayName("?????? ????????? ?????? ????????? ?????? ?????? ??????")
    @Test
    void sendAuthCodeForSignUp(TestInfo testInfo) throws Exception {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("email", "mytamin@naver.com");

        doNothing().when(emailService).sendAuthCodeForSignUp(any());

        //when
        ResultActions actions = mockMvc.perform(post("/auth/signup/code")
                .content(objectMapper.writeValueAsString(map))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        requestFields(
                                fieldWithPath("email").description("*????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????")
                        ))
                );
    }

    @DisplayName("???????????? ???????????? ?????? ????????? ?????? ?????? ??????")
    @Test
    void sendAuthCodeForReset(TestInfo testInfo) throws Exception {
        //given
        Map<String, String> map = new HashMap<>();
        map.put("email", "mytamin@naver.com");

        doNothing().when(emailService).sendAuthCodeForSignUp(any());

        //when
        ResultActions actions = mockMvc.perform(post("/auth/reset/code")
                .content(objectMapper.writeValueAsString(map))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        requestFields(
                                fieldWithPath("email").description("*????????? ?????????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????")
                        ))
                );
    }

    @DisplayName("????????? ?????? ?????? ??????")
    @Test
    void confirmAuthCode(TestInfo testInfo) throws Exception {
        //given
        EmailCheckRequest emailCheckRequest = new EmailCheckRequest(
                "mytamin@naver.com",
                "xx0m98Fw"
        );

        given(emailService.confirmAuthCode(any())).willReturn(true);

        //when
        ResultActions actions = mockMvc.perform(post("/auth/code")
                .content(objectMapper.writeValueAsString(emailCheckRequest))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        requestFields(
                                fieldWithPath("email").description("*????????? ?????????"),
                                fieldWithPath("authCode").description("*???????????? ????????? ????????????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data").description("?????? ?????? ?????? ??????")
                        ))
                );
    }

    @Nested
    @DisplayName("???????????? ?????????")
    class ResetPasswordTest {

        @DisplayName("??????")
        @Test
        void resetPassword(TestInfo testInfo) throws Exception {
            //given
            Map<String, String> map = new HashMap<>();
            map.put("email", "mytamin@naver.com");
            map.put("password", "newnew1234!");

            doNothing().when(authService).resetPassword(any(), any());

            //when
            ResultActions actions = mockMvc.perform(put("/auth/password")
                    .content(objectMapper.writeValueAsString(map))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestFields(
                                    fieldWithPath("email").description("*?????????"),
                                    fieldWithPath("password").description("*???????????? ???????????? (8 ~ 30???)")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ??????")
        @Test
        void resetPassword_3000(TestInfo testInfo) throws Exception {
            //given
            Map<String, String> map = new HashMap<>();
            map.put("email", "mytamin@naver.com");
            map.put("password", "newnew1234!");

            doThrow(new MytaminException(USER_NOT_FOUND_ERROR)).when(authService).resetPassword(any(), any());

            //when
            ResultActions actions = mockMvc.perform(put("/auth/password")
                    .content(objectMapper.writeValueAsString(map))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(3000))
                    .andExpect(jsonPath("errorName").value("USER_NOT_FOUND_ERROR"))
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

}