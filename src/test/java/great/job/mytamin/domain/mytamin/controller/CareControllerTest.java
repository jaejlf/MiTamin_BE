package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.dto.request.CareRequest;
import great.job.mytamin.domain.mytamin.dto.request.CareSearchFilter;
import great.job.mytamin.domain.mytamin.dto.response.CareHistoryResponse;
import great.job.mytamin.domain.mytamin.dto.response.CareResponse;
import great.job.mytamin.domain.mytamin.dto.response.RandomCareResponse;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
@DisplayName("Care ????????????")
class CareControllerTest extends CommonControllerTest {

    String docId = "/care/";

    @MockBean
    private CareService careService;

    @Nested
    @DisplayName("?????? ????????????")
    class CreateCareTest {

        CareRequest careRequest = new CareRequest(
                1,
                "?????? ??? ?????? ?????? ??????",
                "????????? ???????????? ??? ????????? ??????"
        );

        @DisplayName("??????")
        @Test
        void createCare(TestInfo testInfo) throws Exception {
            //given
            given(careService.createCare(any(), any())).willReturn(
                    CareResponse.builder()
                            .careId(1L)
                            .canEdit(true)
                            .careCategory("????????? ??? ???")
                            .careMsg1("?????? ??? ?????? ?????? ??????")
                            .careMsg2("????????? ???????????? ??? ????????? ??????")
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
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            requestFields(
                                    fieldWithPath("careCategoryCode").description("*?????? ???????????? ??????"),
                                    fieldWithPath("careMsg1").description("*?????? ?????? ????????? 1"),
                                    fieldWithPath("careMsg2").description("*?????? ?????? ????????? 2")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????"),
                                    fieldWithPath("data.careId").description("?????? ?????? id"),
                                    fieldWithPath("data.canEdit").description("'?????? ??????' ?????? ?????? ??????"),
                                    fieldWithPath("data.careCategory").description("?????? ????????????"),
                                    fieldWithPath("data.careMsg1").description("?????? ?????? ????????? 1"),
                                    fieldWithPath("data.careMsg2").description("?????? ?????? ????????? 2")
                            ))
                    );
        }

        @DisplayName("?????? ?????? ?????? ??????")
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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("?????? ???????????? ?????? ??????")
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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("?????? ?????? ??????")
    class GetCareTest {

        Long careId = 1L;

        @DisplayName("??????")
        @Test
        void getCare(TestInfo testInfo) throws Exception {
            //given
            given(careService.getCare(any(), any())).willReturn(
                    CareResponse.builder()
                            .careId(1L)
                            .canEdit(true)
                            .careCategory("????????? ??? ???")
                            .careMsg1("?????? ??? ?????? ?????? ??????")
                            .careMsg2("????????? ???????????? ??? ????????? ??????")
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
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            pathParameters(
                                    parameterWithName("careId").description("*?????? ?????? id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????"),
                                    fieldWithPath("data.careId").description("?????? ?????? id"),
                                    fieldWithPath("data.canEdit").description("'?????? ??????' ?????? ?????? ??????"),
                                    fieldWithPath("data.careCategory").description("?????? ????????????"),
                                    fieldWithPath("data.careMsg1").description("?????? ?????? ????????? 1"),
                                    fieldWithPath("data.careMsg2").description("?????? ?????? ????????? 2")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ?????? ?????? id")
        @Test
        void getCare_5002(TestInfo testInfo) throws Exception {
            //given
            given(careService.getCare(any(), any())).willThrow(new MytaminException(CARE_NOT_FOUND_ERROR));

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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("?????? ?????? ??????")
    class UpdateCareTest {

        Long careId = 1L;
        CareRequest careRequest = new CareRequest(
                2,
                "?????? ????????? ???????????? ??????",
                "^,^"
        );

        @DisplayName("??????")
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
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            pathParameters(
                                    parameterWithName("careId").description("*????????? ?????? ?????? id")
                            ),
                            requestFields(
                                    fieldWithPath("careCategoryCode").description("*????????? ?????? ???????????? ??????"),
                                    fieldWithPath("careMsg1").description("*????????? ?????? ?????? ????????? 1"),
                                    fieldWithPath("careMsg2").description("*????????? ?????? ?????? ????????? 2")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ?????? ?????? id")
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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("?????? ?????? ??????")
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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

    }

    @DisplayName("?????? ?????? ?????? ??????")
    @Test
    void getRandomCare(TestInfo testInfo) throws Exception {
        //given
        given(careService.getRandomCare(any())).willReturn(
                RandomCareResponse.builder()
                        .careMsg1("?????? ??? ?????? ?????? ??????")
                        .careMsg2("????????? ???????????? ??? ????????? ??????")
                        .takeAt("22.10.19")
                        .build()
        );

        //when
        ResultActions actions = mockMvc.perform(get("/care/random")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data.careMsg1").description("?????? ?????? ????????? 1"),
                                fieldWithPath("data.careMsg2").description("?????? ?????? ????????? 2"),
                                fieldWithPath("data.takeAt").description("???????????? ?????? ??????")
                        ))
                );
    }

    @DisplayName("?????? ?????? ???????????? ??????")
    @Test
    void getCareHistroy(TestInfo testInfo) throws Exception {
        //given
        CareSearchFilter careSearchFilter = new CareSearchFilter(
                List.of(1, 5)
        );

        given(careService.getCareHistroy(any(), any())).willReturn(mockCareHistoryResponse());

        //when
        ResultActions actions = mockMvc.perform(post("/care/list")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .content(objectMapper.writeValueAsString(careSearchFilter))
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                        ),
                        requestFields(
                                fieldWithPath("careCategoryCodeList").description("???????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data.*[]").description("???/?????? ???????????? ?????? ?????? ?????????"),
                                fieldWithPath("data.*[].careMsg1").description("?????? ?????? ????????? 1"),
                                fieldWithPath("data.*[].careMsg2").description("?????? ?????? ????????? 2"),
                                fieldWithPath("data.*[].careCategory").description("?????? ????????????"),
                                fieldWithPath("data.*[].takeAt").description("???????????? ?????? ??????")
                        ))
                );
    }

    private Map<String, List<CareHistoryResponse>> mockCareHistoryResponse() {
        List<CareHistoryResponse> list_oct = new ArrayList<>();
        list_oct.add(CareHistoryResponse.builder()
                .careMsg1("?????? ??? ?????? ?????? ??????")
                .careMsg2("????????? ???????????? ??? ????????? ??????")
                .careCategory("#????????? ??? ???")
                .takeAt("10.13.Thu")
                .build());
        list_oct.add(CareHistoryResponse.builder()
                .careMsg1("????????? ?????? ?????? ?????????")
                .careMsg2("????????? ??? ????????? ????????? !")
                .careCategory("#???????????? ????????? ?????????")
                .takeAt("10.19.Wed")
                .build());

        Map<String, List<CareHistoryResponse>> careHistory = new HashMap<>();
        careHistory.put("2022??? 10???", list_oct);
        return careHistory;
    }

}