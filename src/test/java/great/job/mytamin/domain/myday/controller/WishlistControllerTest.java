package great.job.mytamin.domain.myday.controller;

import great.job.mytamin.domain.myday.dto.response.WishResponse;
import great.job.mytamin.domain.myday.dto.response.WishlistResponse;
import great.job.mytamin.domain.myday.service.WishService;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
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
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}"));

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
                                fieldWithPath("data.published[]").description("공개된 위시 리스트"),
                                fieldWithPath("data.hidden[]").description("숨겨진 위시 리스트"),
                                fieldWithPath("data.*[].wishId").description("위시 리스트 id"),
                                fieldWithPath("data.*[].wishText").description("위시 텍스트"),
                                fieldWithPath("data.*[].count").description("위시가 기록된 횟수")
                        ))
                );
    }

    private WishlistResponse mockWishlistResponse() {
        List<WishResponse> published = new ArrayList<>();
        published.add(WishResponse.builder()
                .wishId(1L)
                .wishText("소품샵 다녀오기")
                .count(3)
                .build());
        published.add(WishResponse.builder()
                .wishId(2L)
                .wishText("도서관에 가서 책 한권 빌려오기")
                .count(0)
                .build());
        published.add(WishResponse.builder()
                .wishId(3L)
                .wishText("빵 나오는 시간에 맞춰서 갓 나온 빵 사기")
                .count(1)
                .build());

        List<WishResponse> hidden = new ArrayList<>();
        hidden.add(WishResponse.builder()
                .wishId(4L)
                .wishText("영화관에서 제일 빠른 영화 하나 보기")
                .count(0)
                .build());

        return WishlistResponse.of(published, hidden);
    }

}