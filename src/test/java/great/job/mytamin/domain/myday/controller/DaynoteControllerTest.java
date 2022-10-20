package great.job.mytamin.domain.myday.controller;

import great.job.mytamin.domain.myday.dto.request.DaynoteRequest;
import great.job.mytamin.domain.myday.dto.request.DaynoteUpdateRequest;
import great.job.mytamin.domain.myday.dto.response.DaynoteListResponse;
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
import java.util.HashMap;
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
@DisplayName("Daynote 컨트롤러")
class DaynoteControllerTest extends CommonControllerTest {

    String docId = "/daynote/";

    @MockBean
    private DaynoteService daynoteService;

    @Nested
    @DisplayName("데이노트 작성 가능 여부")
    class CanCreateDaynoteTest {

        String performedAt = "2022.10";

        @DisplayName("성공")
        @Test
        void canCreateDaynote(TestInfo testInfo) throws Exception {
            //given
            given(daynoteService.canCreateDaynote(any(), any())).willReturn(true);

            //when
            ResultActions actions = mockMvc.perform(get("/daynote/check/{performedAt}", performedAt)
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
                                    parameterWithName("performedAt").description("*데이노트 작성 날짜 (yyyy.MM)")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data").description("데이노트 작성 가능 여부")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("데이노트 작성하기")
    class CreateDaynoteTest {

        List<MultipartFile> fileList = List.of(
                new MockMultipartFile("file", "mock1.jpg", "image/jpg", "<<image>>".getBytes()),
                new MockMultipartFile("file", "mock2.jpg", "image/jpg", "<<image>>".getBytes())
        );

        MockMultipartFile multipartFileList = new MockMultipartFile(
                "fileList",
                fileList.toString(),
                "image/jpg", "<<image>>".getBytes()
        );

        DaynoteRequest daynoteRequest = new DaynoteRequest(
                fileList,
                "빵 나오는 시간에 맞춰서 갓 나온 빵 사기",
                "따끈따끈한 빵을 샀다. 맛있었따 :0",
                "2022.10"
        );

        @DisplayName("성공")
        @Test
        void createDaynote(TestInfo testInfo) throws Exception {
            //given
            given(daynoteService.createDaynote(any(), any())).willReturn(mockDaynoteResponseOfDetail());

            //when
            ResultActions actions = mockMvc.perform(multipart("/daynote/new")
                    .file(multipartFileList)
                    .param("wishText", daynoteRequest.getWishText())
                    .param("note", daynoteRequest.getNote())
                    .param("performedAt", daynoteRequest.getPerformedAt())
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(MULTIPART_FORM_DATA));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("data").exists())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestParts(
                                    partWithName("fileList").description("*업로드할 이미지 리스트 (.png, .jpg, .jpeg)")
                            ),
                            requestParameters(
                                    parameterWithName("wishText").description("*위시 텍스트"),
                                    parameterWithName("note").description("*데이노트 코멘트"),
                                    parameterWithName("performedAt").description("*데이노트 수행 날짜 정보")
                            ),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.daynoteId").description("데이노트 id"),
                                    fieldWithPath("data.performedAt").description("데이노트 수행 날짜 정보"),
                                    fieldWithPath("data.imgList[]").description("데이노트 이미지 리스트"),
                                    fieldWithPath("data.wishText").description("위시 텍스트"),
                                    fieldWithPath("data.note").description("데이노트 코멘트")
                            ))
                    );
        }

        @DisplayName("이미 해당 년/월에 데이노트 존재")
        @Test
        void createDaynote_8004(TestInfo testInfo) throws Exception {
            //given
            given(daynoteService.createDaynote(any(), any())).willThrow(new MytaminException(DAYNOTE_ALREADY_EXIST_ERROR));

            //when
            ResultActions actions = mockMvc.perform(multipart("/daynote/new")
                    .file(multipartFileList)
                    .param("wishText", daynoteRequest.getWishText())
                    .param("note", daynoteRequest.getNote())
                    .param("performedAt", daynoteRequest.getPerformedAt())
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

        @DisplayName("이미지 최대 업로드 개수 초과")
        @Test
        void createDaynote_6003(TestInfo testInfo) throws Exception {
            //given
            given(daynoteService.createDaynote(any(), any())).willThrow(new MytaminException(FILE_MAXIMUM_EXCEED));

            //when
            ResultActions actions = mockMvc.perform(multipart("/daynote/new")
                    .file(multipartFileList)
                    .param("wishText", daynoteRequest.getWishText())
                    .param("note", daynoteRequest.getNote())
                    .param("performedAt", daynoteRequest.getPerformedAt())
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("데이노트 조회")
    class GetDaynoteTest {

        Long daynoteId = 1L;

        @DisplayName("성공")
        @Test
        void getDaynote(TestInfo testInfo) throws Exception {
            //given
            given(daynoteService.getDaynote(any())).willReturn(mockDaynoteResponseOfDetail());

            //when
            ResultActions actions = mockMvc.perform(get("/daynote/{daynoteId}", daynoteId)
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
                                    parameterWithName("daynoteId").description("*조회할 데이노트 id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.daynoteId").description("데이노트 id"),
                                    fieldWithPath("data.performedAt").description("데이노트 수행 날짜 정보"),
                                    fieldWithPath("data.imgList[]").description("데이노트 이미지 리스트"),
                                    fieldWithPath("data.wishText").description("위시 텍스트"),
                                    fieldWithPath("data.note").description("데이노트 코멘트")
                            ))
                    );
        }

        @DisplayName("존재하지 않는 데이노트 id")
        @Test
        void getDaynote_8003(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(DAYNOTE_NOT_FOUND_ERROR)).when(daynoteService).getDaynote(any());

            //when
            ResultActions actions = mockMvc.perform(get("/daynote/{daynoteId}", daynoteId)
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("데이노트 수정하기")
    class UpdateDaynoteTest {

        Long daynoteId = 1L;

        List<MultipartFile> fileList = List.of(
                new MockMultipartFile("file", "mock1.jpg", "image/jpg", "<<image>>".getBytes()),
                new MockMultipartFile("file", "mock2.jpg", "image/jpg", "<<image>>".getBytes())
        );

        MockMultipartFile multipartFileList = new MockMultipartFile(
                "fileList",
                fileList.toString(),
                "image/jpg", "<<image>>".getBytes()
        );

        DaynoteUpdateRequest daynoteUpdateRequest = new DaynoteUpdateRequest(
                fileList,
                "빵 나오는 시간에 맞춰서 갓 나온 빵 사기",
                "따끈따끈한 빵을 샀다. 맛있었따 :0 **수정수정** 다시 생각해보니까 그냥 그랬다,,"
        );

        @DisplayName("성공")
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
                    .param("wishText", daynoteUpdateRequest.getWishText())
                    .param("note", daynoteUpdateRequest.getNote())
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(MULTIPART_FORM_DATA));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestParts(
                                    partWithName("fileList").description("*업로드할 이미지 리스트 (.png, .jpg, .jpeg)")
                            ),
                            requestParameters(
                                    parameterWithName("wishText").description("*위시 텍스트"),
                                    parameterWithName("note").description("*데이노트 코멘트")
                            ),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("daynoteId").description("*수정할 데이노트 id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지")
                            ))
                    );
        }

        @DisplayName("존재하지 않는 데이노트 id")
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
                    .param("wishText", daynoteUpdateRequest.getWishText())
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("데이노트 삭제하기")
    class DeleteDaynoteTest {

        Long daynoteId = 1L;

        @DisplayName("성공")
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
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("daynoteId").description("*수정할 데이노트 id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지")
                            ))
                    );
        }

        @DisplayName("존재하지 않는 데이노트 id")
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @DisplayName("데이노트 리스트 조회")
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
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data.daynoteList.*[]").description("연도별로 그룹핑된 데이노트 리스트"),
                                fieldWithPath("data.daynoteList.*[].daynoteId").description("데이노트 id"),
                                fieldWithPath("data.daynoteList.*[].month").description("데이노트가 작성된 '월'"),
                                fieldWithPath("data.daynoteList.*[].performedAt").description("데이노트 수행 날짜 정보"),
                                fieldWithPath("data.daynoteList.*[].imgList[]").description("데이노트 이미지 리스트")
                        ))
                );
    }

    private DaynoteResponse mockDaynoteResponseOfDetail() {
        List<String> imgList = new ArrayList<>();
        imgList.add("{{IMAGE_URL_1}}");
        imgList.add("{{IMAGE_URL_2}}");

        return DaynoteResponse.builder()
                .year(2022)
                .daynoteId(1L)
                .performedAt("22년 10월의 마이데이")
                .imgList(imgList)
                .wishText("도서관에 가서 책 한권 빌려오기")
                .note("백만년만에 도서관에 가서 책을 빌렸다. 이번엔 연체되기 전에 꼭 다 읽고 반납해야겠다 *_*")
                .build();
    }

    private DaynoteListResponse mockDaynoteListResponse() {
        List<String> imgList = new ArrayList<>();
        imgList.add("{{IMAGE_URL_1}}");
        imgList.add("{{IMAGE_URL_2}}");

        List<DaynoteResponse> list_2021 = new ArrayList<>();
        list_2021.add(DaynoteResponse.builder()
                .year(2021)
                .daynoteId(1L)
                .month("12")
                .performedAt("21년 12월의 마이데이")
                .imgList(imgList)
                .build());

        List<DaynoteResponse> list_2022 = new ArrayList<>();
        list_2022.add(DaynoteResponse.builder()
                .year(2022)
                .daynoteId(2L)
                .month("05")
                .performedAt("22년 05월의 마이데이")
                .imgList(imgList)
                .build());
        list_2022.add(DaynoteResponse.builder()
                .year(2022)
                .daynoteId(3L)
                .month("10")
                .performedAt("22년 10월의 마이데이")
                .imgList(imgList)
                .build());

        Map<Integer, List<DaynoteResponse>> daynoteListMap = new HashMap<>();
        daynoteListMap.put(2021, list_2021);
        daynoteListMap.put(2022, list_2022);
        return DaynoteListResponse.of(daynoteListMap);
    }

}