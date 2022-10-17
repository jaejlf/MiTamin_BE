package great.job.mytamin.domain.myday.controller;

import great.job.mytamin.global.support.CommonControllerTest;
import great.job.mytamin.domain.myday.service.WishService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(controllers = WishlistController.class)
@DisplayName("Wishlist 컨트롤러")
class WishlistControllerTest extends CommonControllerTest {

    String docId = "/wish/";

    @MockBean
    private WishService wishService;

    @DisplayName("위시 리스트 조회")
    @Test
    void getWishlist(TestInfo testInfo) throws Exception {

    }

    // 화면 설계서 변경 사항 반영 -> 수정/삭제 API 테스트

}