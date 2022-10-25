package great.job.mytamin.domain.myday.controller;

import great.job.mytamin.domain.myday.dto.request.WishRequest;
import great.job.mytamin.domain.myday.dto.response.WishResponse;
import great.job.mytamin.domain.myday.service.WishService;
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
import java.util.List;

import static great.job.mytamin.global.exception.ErrorMap.WISH_ALREADY_EXIST_ERROR;
import static great.job.mytamin.global.exception.ErrorMap.WISH_NOT_FOUND_ERROR;
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

@WebMvcTest(controllers = WishlistController.class)
@DisplayName("Wishlist 컨트롤러")
class WishlistControllerTest extends CommonControllerTest {

    String docId = "/wish/";

    @MockBean
    private WishService wishService;

    @DisplayName("위시 리스트 조회")
    @Test
    void getWishlist(TestInfo testInfo) throws Exception {
        //given
        given(wishService.getWishlist(any())).willReturn(mockWishlistResponse());

        //when
        ResultActions actions = mockMvc.perform(get("/wish/list")
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
                                fieldWithPath("data[]").description("위시 리스트"),
                                fieldWithPath("data[].wishId").description("위시 id"),
                                fieldWithPath("data[].wishText").description("위시 텍스트"),
                                fieldWithPath("data[].count").description("위시가 기록된 횟수")
                        ))
                );
    }

    @Nested
    @DisplayName("위시 생성")
    class CreateWishTest {

        WishRequest wishRequest = new WishRequest(
                "소품샵 다녀오기"
        );

        @DisplayName("성공")
        @Test
        void createWish(TestInfo testInfo) throws Exception {
            //given
            given(wishService.createWish(any(), any())).willReturn(mockWishResponse());

            //when
            ResultActions actions = mockMvc.perform(post("/wish/new")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(wishRequest))
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
                                    fieldWithPath("wishText").description("*위시 텍스트")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지"),
                                    fieldWithPath("data.wishId").description("위시 id"),
                                    fieldWithPath("data.wishText").description("위시 텍스트"),
                                    fieldWithPath("data.count").description("위시가 기록된 횟수")
                            ))
                    );
        }

        @DisplayName("이미 존재하는 위시 리스트")
        @Test
        void createWish_8001(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(WISH_ALREADY_EXIST_ERROR)).when(wishService).createWish(any(), any());

            //when
            ResultActions actions = mockMvc.perform(post("/wish/new")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(wishRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isConflict())
                    .andExpect(jsonPath("errorCode").value(8001))
                    .andExpect(jsonPath("errorName").value("WISH_ALREADY_EXIST_ERROR"))
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
    @DisplayName("위시 수정")
    class UpdateWishTest {

        Long wishId = 1L;

        WishRequest wishRequest = new WishRequest(
                "소품샵 가기"
        );

        @DisplayName("성공")
        @Test
        void updateWish(TestInfo testInfo) throws Exception {
            //given
            doNothing().when(wishService).updateWish(any(), any(), any());

            //when
            ResultActions actions = mockMvc.perform(put("/wish/{wishId}", wishId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(wishRequest))
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
                                    parameterWithName("wishId").description("*수정할 위시 id")
                            ),
                            requestFields(
                                    fieldWithPath("wishText").description("*위시 텍스트")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지")
                            ))
                    );
        }

        @DisplayName("존재하지 않는 위시 id")
        @Test
        void updateWish_8000(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(WISH_NOT_FOUND_ERROR)).when(wishService).updateWish(any(), any(), any());

            //when
            ResultActions actions = mockMvc.perform(put("/wish/{wishId}", wishId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(wishRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(8000))
                    .andExpect(jsonPath("errorName").value("WISH_NOT_FOUND_ERROR"))
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
    @DisplayName("위시 삭제")
    class DeleteWishTest {

        Long wishId = 1L;

        @DisplayName("성공")
        @Test
        void deleteWish(TestInfo testInfo) throws Exception {
            //given
            doNothing().when(wishService).deleteWish(any(), any());

            //when
            ResultActions actions = mockMvc.perform(delete("/wish/{wishId}", wishId)
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
                                    parameterWithName("wishId").description("*삭제할 위시 id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지")
                            ))
                    );
        }

        @DisplayName("존재하지 않는 위시 id")
        @Test
        void deleteWish_8000(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(WISH_NOT_FOUND_ERROR)).when(wishService).deleteWish(any(), any());

            //when
            ResultActions actions = mockMvc.perform(delete("/wish/{wishId}", wishId)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isNotFound())
                    .andExpect(jsonPath("errorCode").value(8000))
                    .andExpect(jsonPath("errorName").value("WISH_NOT_FOUND_ERROR"))
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

    private List<WishResponse> mockWishlistResponse() {
        List<WishResponse> wishResponseList = new ArrayList<>();
        wishResponseList.add(WishResponse.builder()
                .wishId(1L)
                .wishText("소품샵 다녀오기")
                .count(3)
                .build());
        wishResponseList.add(WishResponse.builder()
                .wishId(2L)
                .wishText("도서관에 가서 책 한권 빌려오기")
                .count(0)
                .build());
        wishResponseList.add(WishResponse.builder()
                .wishId(3L)
                .wishText("빵 나오는 시간에 맞춰서 갓 나온 빵 사기")
                .count(1)
                .build());
        return wishResponseList;
    }

    private WishResponse mockWishResponse() {
        return WishResponse.builder()
                .wishId(1L)
                .wishText("소품샵 다녀오기")
                .count(0)
                .build();
    }

}