package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.dto.request.ProfileUpdateRequest;
import great.job.mytamin.domain.user.dto.response.ProfileResponse;
import great.job.mytamin.domain.user.service.UserService;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;

import static great.job.mytamin.global.exception.ErrorMap.NICKNAME_DUPLICATE_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
@DisplayName("User 컨트롤러")
class UserControllerTest extends CommonControllerTest {

    String docId = "/user/";

    @MockBean
    private UserService userService;

    @DisplayName("프로필 조회")
    @Test
    void getProfile(TestInfo testInfo) throws Exception {
        //given
        given(userService.getProfile(any())).willReturn(ProfileResponse.of(user));

        // when
        ResultActions actions = mockMvc.perform(get("/user/profile")
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
                                fieldWithPath("data.nickname").description("닉네임"),
                                fieldWithPath("data.profileImgUrl").description("프로필 이미지 URL"),
                                fieldWithPath("data.beMyMessage").description("'되고싶은 내 모습' 메세지")
                        ))
                );
    }

    @Nested
    @DisplayName("프로필 편집")
    class UpdateProfileTest {

        MockMultipartFile file = new MockMultipartFile("file", "mock1.jpg", "image/jpg", "<<image>>".getBytes());
        ProfileUpdateRequest profileUpdateRequest = new ProfileUpdateRequest(
                file,
                "T",
                "멘탈짱",
                "꾸준히 글을 쓰는"
        );
        
        @DisplayName("성공")
        @Test
        void updateProfile(TestInfo testInfo) throws Exception {
            //given
            doNothing().when(userService).updateProfile(any(), any());

            // when
            MockMultipartHttpServletRequestBuilder builder = multipart("/user/profile");
            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });

            ResultActions actions = mockMvc.perform(builder
                    .file(file)
                    .param("isImgEdited", profileUpdateRequest.getIsImgEdited())
                    .param("nickname", profileUpdateRequest.getNickname())
                    .param("beMyMessage", profileUpdateRequest.getBeMyMessage())
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(MULTIPART_FORM_DATA));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestParts(
                                    partWithName("file").description("*업로드할 이미지 (.png, .jpg, .jpeg)")
                            ),
                            requestParameters(
                                    parameterWithName("isImgEdited").description("*프로필 이미지 수정 여부"),
                                    parameterWithName("nickname").description("*수정할 닉네임 (1 ~ 9자)"),
                                    parameterWithName("beMyMessage").description("*수정할 '되고 싶은 나' 메세지 (1 ~ 20자)")
                            ),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지")
                            ))
                    );
        }

        @DisplayName("이미 사용 중인 닉네임")
        @Test
        void updateProfile_2003(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(NICKNAME_DUPLICATE_ERROR)).when(userService).updateProfile(any(), any());

            // when
            MockMultipartHttpServletRequestBuilder builder = multipart("/user/profile");
            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });

            ResultActions actions = mockMvc.perform(builder
                    .file(file)
                    .param("isImgEdited", profileUpdateRequest.getIsImgEdited())
                    .param("nickname", profileUpdateRequest.getNickname())
                    .param("beMyMessage", profileUpdateRequest.getBeMyMessage())
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(MULTIPART_FORM_DATA));

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

    @DisplayName("가입 날짜 조회")
    @Test
    void getCreatedAt(TestInfo testInfo) throws Exception {
        //given & when
        ResultActions actions = mockMvc.perform(get("/user/created-at")
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
                                fieldWithPath("data.year").description("가입한 year"),
                                fieldWithPath("data.month").description("가입한 month")
                        ))
                );
    }

}