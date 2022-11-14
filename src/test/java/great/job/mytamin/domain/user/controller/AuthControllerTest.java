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
@DisplayName("Auth 컨트롤러")
class AuthControllerTest extends CommonControllerTest {

    String docId = "/auth/";

    @MockBean
    private AuthService authService;

    @MockBean
    private EmailService emailService;

    @MockBean
    private UserUtil userUtil;

    @Nested
    @DisplayName("회원 가입")
    class SignUpTest {

        SignUpRequest signUpRequest = new SignUpRequest(
                "mytamin@naver.com",
                "password1234",
                "강철멘탈",
                "22",
                "00"
        );

        @DisplayName("성공")
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
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("password").description("*비밀번호 (8 ~ 30자)"),
                                    fieldWithPath("nickname").description("*닉네임 (1 ~ 9자)"),
                                    fieldWithPath("mytaminHour").description("마이타민 섭취 지정 시간 HH (24시간)"),
                                    fieldWithPath("mytaminMin").description("마이타민 섭취 지정 시간 MM")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.email").description("이메일"),
                                    fieldWithPath("data.nickname").description("닉네임"),
                                    fieldWithPath("data.profileImgUrl").description("프로필 이미지 URL (default : null)"),
                                    fieldWithPath("data.beMyMessage").description("'되고싶은 내 모습' 메세지 (default : \"마음 면역력이 높아질\")"),
                                    fieldWithPath("data.mytaminHour").description("마이타민 섭취 지정 시간 HH (24시간) (default : null)"),
                                    fieldWithPath("data.mytaminMin").description("마이타민 섭취 지정 시간 MM (default : null)")
                            ))
                    );
        }

        @DisplayName("이메일 형식 오류")
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("비밀번호 형식 오류")
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("이미 가입된 유저")
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("이미 사용 중인 닉네임")
        @Test
        void signUp_2003(TestInfo testInfo) throws Exception {
            //given
            given(authService.signUp(any())).willThrow(new MytaminException(NICKNAME_DUPLICATE_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/auth/signup")
                    .content(objectMapper.writeValueAsString(signUpRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("errorCode").value(2003))
                    .andExpect(jsonPath("errorName").value("NICKNAME_DUPLICATE_ERROR"))
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

    @Nested
    @DisplayName("기본 로그인")
    class DefaultLoginTest {

        LoginRequest loginRequest = new LoginRequest(
                "mytamin@naver.com",
                "password1234",
                "{{FCM TOKEN}}"
        );

        @DisplayName("성공")
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
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("password").description("*비밀번호 (8 ~ 30자)"),
                                    fieldWithPath("fcmToken").description("*FCM 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.accessToken").description("액세스 토큰 (유효 기간 : 30일)"),
                                    fieldWithPath("data.refreshToken").description("리프레쉬 토큰 (유효 기간 : 180일)")
                            ))
                    );
        }

        @DisplayName("가입되지 않은 유저")
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("잘못된 비밀번호")
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @DisplayName("이메일 중복 체크")
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
                                parameterWithName("email").description("*이메일")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data").description("이메일 중복 여부")
                        ))
                );
    }

    @DisplayName("닉네임 중복 체크")
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
                                parameterWithName("nickname").description("*닉네임")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data").description("닉네임 중복 여부")
                        ))
                );
    }

    @Nested
    @DisplayName("토큰 재발급")
    class ReissueTest {

        ReissueRequest tokenRequest = new ReissueRequest(
                "mytamin@naver.com",
                "{{REFRESH_TOKEN}}"
        );

        @DisplayName("성공")
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
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("refreshToken").description("*리프레쉬 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.accessToken").description("액세스 토큰 (유효 기간 : 30일)"),
                                    fieldWithPath("data.refreshToken").description("리프레쉬 토큰 (유효 기간 : 180일)")
                            ))
                    );
        }

        @DisplayName("가입되지 않은 유저")
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("DB에 저장된 토큰과 불일치")
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @DisplayName("회원 가입을 위한 이메일 인증 코드 전송")
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
                                fieldWithPath("email").description("*인증할 이메일")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지")
                        ))
                );
    }

    @DisplayName("비밀번호 재설정을 위한 이메일 인증 코드 전송")
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
                                fieldWithPath("email").description("*인증할 이메일")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지")
                        ))
                );
    }

    @DisplayName("이메일 인증 코드 확인")
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
                                fieldWithPath("email").description("*인증할 이메일"),
                                fieldWithPath("authCode").description("*사용자가 입력한 인증번호")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data").description("인증 코드 일치 여부")
                        ))
                );
    }

    @Nested
    @DisplayName("비밀번호 재설정")
    class ResetPasswordTest {

        @DisplayName("성공")
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
                                    fieldWithPath("email").description("*이메일"),
                                    fieldWithPath("password").description("*재설정할 비밀번호 (8 ~ 30자)")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지")
                            ))
                    );
        }

        @DisplayName("가입되지 않은 유저")
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

}