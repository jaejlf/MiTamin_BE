package great.job.mytamin.domain.myday.controller;

import great.job.mytamin.domain.myday.dto.request.DaynoteRequest;
import great.job.mytamin.domain.myday.dto.request.DaynoteUpdateRequest;
import great.job.mytamin.domain.myday.dto.response.DaynoteResponse;
import great.job.mytamin.domain.myday.service.DaynoteService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static great.job.mytamin.global.exception.ErrorMap.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.*;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = DaynoteController.class)
@DisplayName("Daynote ????????????")
class DaynoteControllerTest extends CommonControllerTest {

    String docId = "/daynote/";

    @MockBean
    private DaynoteService daynoteService;

    @DisplayName("???????????? ?????? ?????? ??????")
    @Test
    void canCreateDaynote(TestInfo testInfo) throws Exception {
        //given
        String date = "2022.10";
        given(daynoteService.canCreateDaynote(any(), any())).willReturn(true);

        //when
        ResultActions actions = mockMvc.perform(get("/daynote/check/{date}", date)
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
                                parameterWithName("date").description("*???????????? ?????? ?????? (yyyy.MM)")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data").description("???????????? ?????? ?????? ??????")
                        ))
                );
    }

    @Nested
    @DisplayName("???????????? ????????????")
    class CreateDaynoteTest {

        List<MultipartFile> fileList = List.of(
                new MockMultipartFile("file", "mock1.jpg", "image/jpg", "<<image>>" .getBytes()),
                new MockMultipartFile("file", "mock2.jpg", "image/jpg", "<<image>>" .getBytes())
        );

        MockMultipartFile multipartFileList = new MockMultipartFile(
                "fileList",
                fileList.toString(),
                "image/jpg", "<<image>>" .getBytes()
        );

        DaynoteRequest daynoteRequest = new DaynoteRequest(
                fileList,
                "1",
                "??????????????? ?????? ??????. ???????????? :0",
                "2022.10"
        );

        @DisplayName("??????")
        @Test
        void createDaynote(TestInfo testInfo) throws Exception {
            //given
            given(daynoteService.createDaynote(any(), any())).willReturn(mockDaynoteResponseOfDetail());

            //when
            ResultActions actions = mockMvc.perform(multipart("/daynote/new")
                    .file(multipartFileList)
                    .param("wishId", daynoteRequest.getWishId())
                    .param("note", daynoteRequest.getNote())
                    .param("date", daynoteRequest.getDate())
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(MULTIPART_FORM_DATA));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestParts(
                                    partWithName("fileList").description("*???????????? ????????? ????????? (.png, .jpg, .jpeg)")
                            ),
                            requestParameters(
                                    parameterWithName("wishId").description("*?????? id"),
                                    parameterWithName("note").description("*???????????? ?????????"),
                                    parameterWithName("date").description("*???????????? ?????? ?????? (yyyy.MM)")
                            ),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????"),
                                    fieldWithPath("data.daynoteId").description("???????????? id"),
                                    fieldWithPath("data.imgList[]").description("???????????? ????????? ?????????"),
                                    fieldWithPath("data.year").description("??????????????? ????????? year"),
                                    fieldWithPath("data.month").description("??????????????? ????????? month"),
                                    fieldWithPath("data.wishId").description("?????? id"),
                                    fieldWithPath("data.wishText").description("?????? ?????????"),
                                    fieldWithPath("data.note").description("???????????? ?????????")
                            ))
                    );
        }

        @DisplayName("?????? ?????? ???/?????? ???????????? ??????")
        @Test
        void createDaynote_8004(TestInfo testInfo) throws Exception {
            //given
            given(daynoteService.createDaynote(any(), any())).willThrow(new MytaminException(DAYNOTE_ALREADY_EXIST_ERROR));

            //when
            ResultActions actions = mockMvc.perform(multipart("/daynote/new")
                    .file(multipartFileList)
                    .param("wishId", daynoteRequest.getWishId())
                    .param("note", daynoteRequest.getNote())
                    .param("date", daynoteRequest.getDate())
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(MULTIPART_FORM_DATA));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("errorCode").value(8004))
                    .andExpect(jsonPath("errorName").value("DAYNOTE_ALREADY_EXIST_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("????????? ?????? ????????? ?????? ??????")
        @Test
        void createDaynote_6003(TestInfo testInfo) throws Exception {
            //given
            given(daynoteService.createDaynote(any(), any())).willThrow(new MytaminException(FILE_MAXIMUM_EXCEED));

            //when
            ResultActions actions = mockMvc.perform(multipart("/daynote/new")
                    .file(multipartFileList)
                    .param("wishId", daynoteRequest.getWishId())
                    .param("note", daynoteRequest.getNote())
                    .param("date", daynoteRequest.getDate())
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(MULTIPART_FORM_DATA));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(6003))
                    .andExpect(jsonPath("errorName").value("FILE_MAXIMUM_EXCEED"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ?????? id")
        @Test
        void createDaynote_8000(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(WISH_NOT_FOUND_ERROR)).when(daynoteService).createDaynote(any(), any());

            //when
            ResultActions actions = mockMvc.perform(multipart("/daynote/new")
                    .file(multipartFileList)
                    .param("wishId", daynoteRequest.getWishId())
                    .param("note", daynoteRequest.getNote())
                    .param("date", daynoteRequest.getDate())
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(MULTIPART_FORM_DATA));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(8000))
                    .andExpect(jsonPath("errorName").value("WISH_NOT_FOUND_ERROR"))
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
    @DisplayName("???????????? ????????????")
    class UpdateDaynoteTest {

        Long daynoteId = 1L;

        List<MultipartFile> fileList = List.of(
                new MockMultipartFile("file", "mock1.jpg", "image/jpg", "<<image>>" .getBytes()),
                new MockMultipartFile("file", "mock2.jpg", "image/jpg", "<<image>>" .getBytes())
        );

        MockMultipartFile multipartFileList = new MockMultipartFile(
                "fileList",
                fileList.toString(),
                "image/jpg", "<<image>>" .getBytes()
        );

        DaynoteUpdateRequest daynoteUpdateRequest = new DaynoteUpdateRequest(
                fileList,
                "1",
                "??????????????? ?????? ??????. ???????????? :0 **????????????** ?????? ?????????????????? ?????? ?????????,,"
        );

        @DisplayName("??????")
        @Test
        void updateDaynote(TestInfo testInfo) throws Exception {
            //given
            doNothing().when(daynoteService).updateDaynote(any(), any(), any());

            //when
            MockMultipartHttpServletRequestBuilder builder = multipart("/daynote/{daynoteId}", daynoteId);
            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });

            ResultActions actions = mockMvc.perform(builder
                    .file(multipartFileList)
                    .param("wishId", daynoteUpdateRequest.getWishId())
                    .param("note", daynoteUpdateRequest.getNote())
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(MULTIPART_FORM_DATA));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestParts(
                                    partWithName("fileList").description("*???????????? ????????? ????????? (.png, .jpg, .jpeg)")
                            ),
                            requestParameters(
                                    parameterWithName("wishId").description("*?????? id"),
                                    parameterWithName("note").description("*???????????? ?????????")
                            ),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            pathParameters(
                                    parameterWithName("daynoteId").description("*????????? ???????????? id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ???????????? id")
        @Test
        void updateDaynote_8003(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(DAYNOTE_NOT_FOUND_ERROR)).when(daynoteService).updateDaynote(any(), any(), any());

            //when
            MockMultipartHttpServletRequestBuilder builder = multipart("/daynote/{daynoteId}", daynoteId);
            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });

            ResultActions actions = mockMvc.perform(builder
                    .file(multipartFileList)
                    .param("wishId", daynoteUpdateRequest.getWishId())
                    .param("note", daynoteUpdateRequest.getNote())
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(MULTIPART_FORM_DATA));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(8003))
                    .andExpect(jsonPath("errorName").value("DAYNOTE_NOT_FOUND_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("????????? ?????? ????????? ?????? ??????")
        @Test
        void updateDaynote_6003(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(FILE_MAXIMUM_EXCEED)).when(daynoteService).updateDaynote(any(), any(), any());

            //when
            MockMultipartHttpServletRequestBuilder builder = multipart("/daynote/{daynoteId}", daynoteId);
            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });

            ResultActions actions = mockMvc.perform(builder
                    .file(multipartFileList)
                    .param("wishId", daynoteUpdateRequest.getWishId())
                    .param("note", daynoteUpdateRequest.getNote())
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(MULTIPART_FORM_DATA));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(6003))
                    .andExpect(jsonPath("errorName").value("FILE_MAXIMUM_EXCEED"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ?????? id")
        @Test
        void updateDaynote_8000(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(WISH_NOT_FOUND_ERROR)).when(daynoteService).updateDaynote(any(), any(), any());

            //when
            MockMultipartHttpServletRequestBuilder builder = multipart("/daynote/{daynoteId}", daynoteId);
            builder.with(request -> {
                request.setMethod("PUT");
                return request;
            });

            ResultActions actions = mockMvc.perform(builder
                    .file(multipartFileList)
                    .param("wishId", daynoteUpdateRequest.getWishId())
                    .param("note", daynoteUpdateRequest.getNote())
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(MULTIPART_FORM_DATA));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(8000))
                    .andExpect(jsonPath("errorName").value("WISH_NOT_FOUND_ERROR"))
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
    @DisplayName("???????????? ????????????")
    class DeleteDaynoteTest {

        Long daynoteId = 1L;

        @DisplayName("??????")
        @Test
        void deleteDaynote(TestInfo testInfo) throws Exception {
            //given
            doNothing().when(daynoteService).deleteDaynote(any(), any());

            //when
            ResultActions actions = mockMvc.perform(delete("/daynote/{daynoteId}", daynoteId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
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
                                    parameterWithName("daynoteId").description("*????????? ???????????? id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ???????????? id")
        @Test
        void deleteDaynote_8003(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(DAYNOTE_NOT_FOUND_ERROR)).when(daynoteService).deleteDaynote(any(), any());

            //when
            ResultActions actions = mockMvc.perform(delete("/daynote/{daynoteId}", daynoteId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(8003))
                    .andExpect(jsonPath("errorName").value("DAYNOTE_NOT_FOUND_ERROR"))
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

    @DisplayName("???????????? ????????? ??????")
    @Test
    void getDaynoteList(TestInfo testInfo) throws Exception {
        //given
        given(daynoteService.getDaynoteList(any())).willReturn(mockDaynoteListResponse());

        //when
        ResultActions actions = mockMvc.perform(get("/daynote/list")
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
                                fieldWithPath("data.*[]").description("???????????? ???????????? ???????????? ?????????"),
                                fieldWithPath("data.*[].daynoteId").description("???????????? id"),
                                fieldWithPath("data.*[].imgList[]").description("???????????? ????????? ?????????"),
                                fieldWithPath("data.*[].year").description("??????????????? ????????? year"),
                                fieldWithPath("data.*[].month").description("??????????????? ????????? month"),
                                fieldWithPath("data.*[].wishId").description("?????? id"),
                                fieldWithPath("data.*[].wishText").description("?????? ?????????"),
                                fieldWithPath("data.*[].note").description("???????????? ?????????")
                        ))
                );
    }

    private DaynoteResponse mockDaynoteResponseOfDetail() {
        List<String> imgList = new ArrayList<>();
        imgList.add("{{IMAGE_URL_1}}");
        imgList.add("{{IMAGE_URL_2}}");

        return DaynoteResponse.builder()
                .daynoteId(1L)
                .imgList(imgList)
                .year(2022)
                .month(10)
                .wishId(1L)
                .wishText("???????????? ?????? ??? ?????? ????????????")
                .note("??????????????? ???????????? ?????? ?????? ?????????. ????????? ???????????? ?????? ??? ??? ?????? ?????????????????? *_*")
                .build();
    }

    private Map<Integer, List<DaynoteResponse>> mockDaynoteListResponse() {
        List<String> imgList = new ArrayList<>();
        imgList.add("{{IMAGE_URL_1}}");
        imgList.add("{{IMAGE_URL_2}}");

        List<DaynoteResponse> list_2021 = new ArrayList<>();
        list_2021.add(DaynoteResponse.builder()
                .daynoteId(1L)
                .imgList(imgList)
                .year(2021)
                .month(12)
                .wishId(1L)
                .wishText("???????????? ?????? ??? ?????? ????????????")
                .note("??????????????? ???????????? ?????? ?????? ?????????. ????????? ???????????? ?????? ??? ??? ?????? ?????????????????? *_*")
                .build());

        List<DaynoteResponse> list_2022 = new ArrayList<>();
        list_2022.add(DaynoteResponse.builder()
                .daynoteId(3L)
                .imgList(imgList)
                .year(2022)
                .month(10)
                .wishId(2L)
                .wishText("????????? ??????")
                .note("????????? ?????? ????????? ????????? ??????.")
                .build());
        list_2022.add(DaynoteResponse.builder()
                .daynoteId(2L)
                .imgList(imgList)
                .year(2022)
                .month(9)
                .wishId(3L)
                .wishText("??? ????????? ????????? ????????? ??? ?????? ??? ?????????")
                .note("??????????????? ????????? ????????? ?????? ?????? !")
                .build());

        Map<Integer, List<DaynoteResponse>> daynoteListMap = new LinkedHashMap<>();
        daynoteListMap.put(2022, list_2022);
        daynoteListMap.put(2021, list_2021);
        return daynoteListMap;
    }

}