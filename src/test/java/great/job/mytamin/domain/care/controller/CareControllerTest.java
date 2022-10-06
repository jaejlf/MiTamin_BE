package great.job.mytamin.domain.care.controller;

import great.job.mytamin.domain.care.dto.request.CareRequest;
import great.job.mytamin.domain.care.dto.response.CareResponse;
import great.job.mytamin.domain.care.service.CareService;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static great.job.mytamin.global.exception.ErrorMap.CARE_ALREADY_DONE;
import static great.job.mytamin.global.exception.ErrorMap.INVALID_CATEGORY_CODE_ERROR;
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

@WebMvcTest(controllers = CareController.class)
@DisplayName("Care 컨트롤러")
class CareControllerTest extends CommonControllerTest {

    @MockBean
    private CareService careService;

    @Nested
    @DisplayName("칭찬 처방하기")
    class CreateCareTest {

        @DisplayName("성공")
        @Test
        void createCare(TestInfo testInfo) throws Exception {
            //given
            CareRequest careRequest = new CareRequest(
                    1,
                    "오늘 할 일을 전부 했어",
                    "성실히 노력하는 내 모습이 좋아"
            );

            given(careService.createCare(any(), any())).willReturn(mockCareResponse());

            //when
            ResultActions actions = mockMvc.perform(post("/care/new")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(careRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document("/care/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("careCategoryCode").description("*칭찬 카테고리 코드"),
                                    fieldWithPath("careMsg1").description("*칭찬 처방 메세지 1"),
                                    fieldWithPath("careMsg2").description("*칭찬 처방 메세지 2")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.careCategory").description("칭찬 카테고리"),
                                    fieldWithPath("data.careMsg1").description("칭찬 처방 메세지 1"),
                                    fieldWithPath("data.careMsg2").description("칭찬 처방 메세지 2")
                            ))
                    );
        }

        @DisplayName("이미 칭찬 처방 완료")
        @Test
        void createCare_5001(TestInfo testInfo) throws Exception {
            //given
            CareRequest careRequest = new CareRequest(
                    1,
                    "오늘 할 일을 전부 했어",
                    "성실히 노력하는 내 모습이 좋아"
            );

            given(careService.createCare(any(), any())).willThrow(new MytaminException(CARE_ALREADY_DONE));

            //when
            ResultActions actions = mockMvc.perform(post("/care/new")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(careRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("errorCode").value(5001))
                    .andExpect(jsonPath("errorName").value("CARE_ALREADY_DONE"))
                    .andDo(document("/care/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("careCategoryCode").description("*칭찬 카테고리 코드"),
                                    fieldWithPath("careMsg1").description("*칭찬 처방 메세지 1"),
                                    fieldWithPath("careMsg2").description("*칭찬 처방 메세지 2")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("칭찬 카테고리 코드 오류")
        @Test
        void createCare_5000(TestInfo testInfo) throws Exception {
            //given
            CareRequest careRequest = new CareRequest(
                    1,
                    "오늘 할 일을 전부 했어",
                    "성실히 노력하는 내 모습이 좋아"
            );
            
            given(careService.createCare(any(), any())).willThrow(new MytaminException(INVALID_CATEGORY_CODE_ERROR));

            //when
            ResultActions actions = mockMvc.perform(post("/care/new")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(careRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(5000))
                    .andExpect(jsonPath("errorName").value("INVALID_CATEGORY_CODE_ERROR"))
                    .andDo(document("/care/" + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("careCategoryCode").description("*칭찬 카테고리 코드"),
                                    fieldWithPath("careMsg1").description("*칭찬 처방 메세지 1"),
                                    fieldWithPath("careMsg2").description("*칭찬 처방 메세지 2")
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

    private CareResponse mockCareResponse() {
        return CareResponse.builder()
                .careCategory("이루어 낸 일")
                .careMsg1("오늘 할 일을 전부 했어")
                .careMsg2("성실히 노력하는 내 모습이 좋아")
                .build();
    }

}