package great.job.mytamin.domain.myday.controller;

import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonControllerTest;
import great.job.mytamin.domain.myday.dto.response.DaynoteListResponse;
import great.job.mytamin.domain.myday.dto.response.DaynoteResponse;
import great.job.mytamin.domain.myday.service.DaynoteService;
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

import static great.job.mytamin.global.exception.ErrorMap.DATETIME_PARSE_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
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

        @DisplayName("성공")
        @Test
        void canCreateDaynote(TestInfo testInfo) throws Exception {
            //given
            String performedAt = "2022.10";

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

        @DisplayName("날짜 입력값 오류")
        @Test
        void canCreateDaynote_7001(TestInfo testInfo) throws Exception {
            //given
            String performedAt = "9999.99";

            given(daynoteService.canCreateDaynote(any(), any())).willThrow(new MytaminException(DATETIME_PARSE_ERROR));

            //when
            ResultActions actions = mockMvc.perform(get("/daynote/check/{performedAt}", performedAt)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(7001))
                    .andExpect(jsonPath("errorName").value("DATETIME_PARSE_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("performedAt").description("*데이노트 작성 날짜 (yyyy.MM)")
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

    @Nested
    @DisplayName("데이노트 작성하기")
    class CreateDaynoteTest {

        @DisplayName("성공")
        @Test
        void createDaynote(TestInfo testInfo) throws Exception {

        }

        @DisplayName("이미 해당 년/월에 데이노트 존재")
        @Test
        void createDaynote_8003(TestInfo testInfo) throws Exception {

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
                                fieldWithPath("data.daynoteList.*[]").description("특정 연도의 데이노트 리스트"),
                                fieldWithPath("data.daynoteList.*[].daynoteId").description("데이노트 id"),
                                fieldWithPath("data.daynoteList.*[].month").description("특정 달의 데이노트"),
                                fieldWithPath("data.daynoteList.*[].performedAt").description("데이노트 날짜 제목"),
                                fieldWithPath("data.daynoteList.*[].imgList[]").description("데이노트 이미지 리스트")
                        ))
                );
    }

    private DaynoteResponse mockDaynoteResponse() {
        List<String> imgList = new ArrayList<>();
        imgList.add("{{IMAGE_URL_1}}");
        imgList.add("{{IMAGE_URL_2}}");

        return DaynoteResponse.builder()
                .year(2022)
                .daynoteId(1L)
                .month("10")
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