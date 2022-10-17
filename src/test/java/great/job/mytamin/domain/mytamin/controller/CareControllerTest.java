package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.dto.request.CareRequest;
import great.job.mytamin.domain.mytamin.dto.response.CareResponse;
import great.job.mytamin.domain.mytamin.service.CareService;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static great.job.mytamin.global.exception.ErrorMap.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = CareController.class)
@DisplayName("Care 컨트롤러")
class CareControllerTest extends CommonControllerTest {

    String docId = "/care/";

    @MockBean
    private CareService careService;

    @Nested
    @DisplayName("칭찬 처방하기")
    class CreateCareTest {

        CareRequest careRequest = new CareRequest(
                1,
                "오늘 할 일을 전부 했어",
                "성실히 노력하는 내 모습이 좋아"
        );

        @DisplayName("성공")
        @Test
        void createCare(TestInfo testInfo) throws Exception {
            //given
            given(careService.createCare(any(), any())).willReturn(
                    CareResponse.builder()
                            .careId(1L)
                            .canEdit(true)
                            .careCategory("이루어 낸 일")
                            .careMsg1("오늘 할 일을 전부 했어")
                            .careMsg2("성실히 노력하는 내 모습이 좋아")
                            .build()
            );

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
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
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
                                    fieldWithPath("data.careId").description("칭찬 처방 id"),
                                    fieldWithPath("data.canEdit").description("'칭찬 처방' 수정 가능 여부"),
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
            given(careService.createCare(any(), any())).willThrow(new MytaminException(CARE_ALREADY_DONE_ERROR));

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
                    .andExpect(jsonPath("errorName").value("CARE_ALREADY_DONE_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
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
    @DisplayName("칭찬 처방 조회")
    class GetCareTest {

        Long careId = 1L;

        @DisplayName("성공")
        @Test
        void getCare(TestInfo testInfo) throws Exception {
            //given
            given(careService.getCare(any())).willReturn(
                    CareResponse.builder()
                            .careId(1L)
                            .canEdit(true)
                            .careCategory("이루어 낸 일")
                            .careMsg1("오늘 할 일을 전부 했어")
                            .careMsg2("성실히 노력하는 내 모습이 좋아")
                            .build()
            );

            //when
            ResultActions actions = mockMvc.perform(get("/care/{careId}", careId)
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
                            pathParameters(
                                    parameterWithName("careId").description("*칭찬 처방 id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.careId").description("칭찬 처방 id"),
                                    fieldWithPath("data.canEdit").description("'칭찬 처방' 수정 가능 여부"),
                                    fieldWithPath("data.careCategory").description("칭찬 카테고리"),
                                    fieldWithPath("data.careMsg1").description("칭찬 처방 메세지 1"),
                                    fieldWithPath("data.careMsg2").description("칭찬 처방 메세지 2")
                            ))
                    );
        }

        @DisplayName("존재하지 않는 칭찬 처방 id")
        @Test
        void getCare_5002(TestInfo testInfo) throws Exception {
            //given
            given(careService.getCare(any())).willThrow(new MytaminException(CARE_NOT_FOUND_ERROR));

            //when
            ResultActions actions = mockMvc.perform(get("/care/{careId}", careId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(5002))
                    .andExpect(jsonPath("errorName").value("CARE_NOT_FOUND_ERROR"))
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
    @DisplayName("칭찬 처방 수정")
    class UpdateCareTest {

        Long careId = 1L;
        CareRequest careRequest = new CareRequest(
                2,
                "칭찬 처방을 수정하고 싶어",
                "^,^"
        );

        @DisplayName("성공")
        @Test
        void updateCare(TestInfo testInfo) throws Exception {
            //given
            doNothing().when(careService).updateCare(any(), any(), any());

            //when
            ResultActions actions = mockMvc.perform(put("/care/{careId}", careId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(careRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("careId").description("*수정할 칭찬 처방 id")
                            ),
                            requestFields(
                                    fieldWithPath("careCategoryCode").description("*수정할 칭찬 카테고리 코드"),
                                    fieldWithPath("careMsg1").description("*수정할 칭찬 처방 메세지 1"),
                                    fieldWithPath("careMsg2").description("*수정할 칭찬 처방 메세지 2")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지")
                            ))
                    );
        }

        @DisplayName("존재하지 않는 칭찬 처방 id")
        @Test
        void updateCare_5002(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(CARE_NOT_FOUND_ERROR)).when(careService).updateCare(any(), any(), any());

            //when
            ResultActions actions = mockMvc.perform(put("/care/{careId}", careId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(careRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(5002))
                    .andExpect(jsonPath("errorName").value("CARE_NOT_FOUND_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("수정 불가 시간")
        @Test
        void updateCare_7000(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(EDIT_TIMEOUT_ERROR)).when(careService).updateCare(any(), any(), any());

            //when
            ResultActions actions = mockMvc.perform(put("/care/{careId}", careId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(careRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(7000))
                    .andExpect(jsonPath("errorName").value("EDIT_TIMEOUT_ERROR"))
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