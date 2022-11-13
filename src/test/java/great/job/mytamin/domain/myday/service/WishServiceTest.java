package great.job.mytamin.domain.myday.service;

import great.job.mytamin.domain.myday.dto.request.WishRequest;
import great.job.mytamin.domain.myday.dto.response.WishResponse;
import great.job.mytamin.domain.myday.entity.Wish;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Wish 서비스")
class WishServiceTest extends CommonServiceTest {

    @Autowired
    private WishService wishService;

    @DisplayName("위시 리스트 조회")
    @Test
    void getWishlist() {
        //given
        wishRepository.save(new Wish("소품샵 가기", user));

        //when
        List<WishResponse> result = wishService.getWishlist(user);

        //then
        List<WishResponse> expected = new ArrayList<>();
        expected.add(WishResponse.builder()
                .wishId(1L)
                .wishText("소품샵 가기")
                .count(0)
                .build());

        assertAll(
                () -> assertThat(result.get(0).getWishText()).isEqualTo(expected.get(0).getWishText()),
                () -> assertThat(result.get(0).getCount()).isEqualTo(expected.get(0).getCount())
        );
    }

    @Nested
    @DisplayName("위시 수정")
    class UpdateWishTest {

        @DisplayName("성공")
        @Test
        void updateWish() {
            //given
            Wish wish = wishRepository.save(new Wish("소품샵 가기", user));

            //when
            wishService.updateWish(user, wish.getWishId(), new WishRequest(
                    "수정된 소품샵 가기"
            ));

            String result = wishRepository.findById(wish.getWishId()).get().getWishText();

            //then
            String expected = "수정된 소품샵 가기";
            assertThat(result).isEqualTo(expected);
        }

        @DisplayName("존재하지 않는 위시 id")
        @Test
        void updateWish_8000() {
            //given & when & then
            assertThatThrownBy(() -> wishService.updateWish(user, 999L, new WishRequest("수정된 소품샵 가기")))
                    .isInstanceOf(MytaminException.class)
                    .hasMessageContaining("WISH_NOT_FOUND_ERROR");
        }

        @DisplayName("이미 존재하는 위시 리스트")
        @Test
        void updateWish_8001() {
            //given
            Wish wish1 = wishRepository.save(new Wish("소품샵 가기", user));
            Wish wish2 = wishRepository.save(new Wish("빵 사먹기", user));

            //when & then
            assertThatThrownBy(() -> wishService.updateWish(user, wish2.getWishId(), new WishRequest("소품샵 가기")))
                    .isInstanceOf(MytaminException.class)
                    .hasMessageContaining("WISH_ALREADY_EXIST_ERROR");
        }

    }

    @Nested
    @DisplayName("위시 삭제")
    class DeleteWishTest {

        @DisplayName("성공")
        @Test
        void deleteWish() {
            //given
            Wish wish = wishRepository.save(new Wish("소품샵 가기", user));

            //when
            wishService.deleteWish(user, wish.getWishId());

            //then
            assertThat(wishRepository.findById(wish.getWishId()).isEmpty()).isEqualTo(true);
        }

        @DisplayName("존재하지 않는 위시 id")
        @Test
        void deleteWish_8000() {
            //given & when & then
            assertThatThrownBy(() -> wishService.deleteWish(user, 999L))
                    .isInstanceOf(MytaminException.class)
                    .hasMessageContaining("WISH_NOT_FOUND_ERROR");
        }

    }

    @Nested
    @DisplayName("위시 생성")
    class CreateWishTest {

        @DisplayName("성공")
        @Test
        void createWish() {
            //given
            String wishText = "소품샵 가기";

            //when
            WishResponse result = wishService.createWish(user, wishText);

            //then
            WishResponse expected = WishResponse.builder()
                    .wishId(1L)
                    .wishText(wishText)
                    .count(0)
                    .build();

            assertAll(
                    () -> assertThat(result.getWishText()).isEqualTo(expected.getWishText()),
                    () -> assertThat(result.getCount()).isEqualTo(expected.getCount())
            );
        }

        @DisplayName("이미 존재하는 위시 리스트")
        @Test
        void createWish_8001() {
            //given
            wishRepository.save(new Wish("소품샵 가기", user));
            String wishText = "소품샵 가기";

            //when & then
            assertThatThrownBy(() -> wishService.createWish(user, wishText))
                    .isInstanceOf(MytaminException.class)
                    .hasMessageContaining("WISH_ALREADY_EXIST_ERROR");
        }

    }

    @DisplayName("위시 전체 삭제")
    @Test
    void deleteAll() {
        //given
        wishRepository.save(new Wish("소품샵 가기", user));
        wishRepository.save(new Wish("빵 사먹기", user));
        wishRepository.save(new Wish("심야 영화 보기", user));

        assertThat(wishRepository.count()).isEqualTo(3);

        //when
        wishService.deleteAll(user);

        //then
        assertThat(wishRepository.count()).isEqualTo(0);
    }

}