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
@DisplayName("Wishlist ????????????")
class WishlistControllerTest extends CommonControllerTest {

    String docId = "/wish/";

    @MockBean
    private WishService wishService;

    @DisplayName("?????? ????????? ??????")
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
                                headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data[]").description("?????? ?????????"),
                                fieldWithPath("data[].wishId").description("?????? id"),
                                fieldWithPath("data[].wishText").description("?????? ?????????"),
                                fieldWithPath("data[].count").description("????????? ????????? ??????")
                        ))
                );
    }

    @Nested
    @DisplayName("?????? ??????")
    class CreateWishTest {

        WishRequest wishRequest = new WishRequest(
                "????????? ????????????"
        );

        @DisplayName("??????")
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
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            requestFields(
                                    fieldWithPath("wishText").description("*?????? ?????????")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????"),
                                    fieldWithPath("data.wishId").description("?????? id"),
                                    fieldWithPath("data.wishText").description("?????? ?????????"),
                                    fieldWithPath("data.count").description("????????? ????????? ??????")
                            ))
                    );
        }

        @DisplayName("?????? ???????????? ?????? ?????????")
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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("?????? ??????")
    class UpdateWishTest {

        Long wishId = 1L;

        WishRequest wishRequest = new WishRequest(
                "????????? ??????"
        );

        @DisplayName("??????")
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
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            pathParameters(
                                    parameterWithName("wishId").description("*????????? ?????? id")
                            ),
                            requestFields(
                                    fieldWithPath("wishText").description("*?????? ?????????")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ?????? id")
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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

    }

    @Nested
    @DisplayName("?????? ??????")
    class DeleteWishTest {

        Long wishId = 1L;

        @DisplayName("??????")
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
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            pathParameters(
                                    parameterWithName("wishId").description("*????????? ?????? id")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("???????????? ?????? ?????? id")
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
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

    }

    private List<WishResponse> mockWishlistResponse() {
        List<WishResponse> wishResponseList = new ArrayList<>();
        wishResponseList.add(WishResponse.builder()
                .wishId(1L)
                .wishText("????????? ????????????")
                .count(3)
                .build());
        wishResponseList.add(WishResponse.builder()
                .wishId(2L)
                .wishText("???????????? ?????? ??? ?????? ????????????")
                .count(0)
                .build());
        wishResponseList.add(WishResponse.builder()
                .wishId(3L)
                .wishText("??? ????????? ????????? ????????? ??? ?????? ??? ??????")
                .count(1)
                .build());
        return wishResponseList;
    }

    private WishResponse mockWishResponse() {
        return WishResponse.builder()
                .wishId(1L)
                .wishText("????????? ????????????")
                .count(0)
                .build();
    }

}