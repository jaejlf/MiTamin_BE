package great.job.mytamin.domain.myday.service;

import great.job.mytamin.domain.myday.dto.request.WishRequest;
import great.job.mytamin.domain.myday.dto.response.WishResponse;
import great.job.mytamin.domain.myday.entity.Wish;
import great.job.mytamin.domain.myday.repository.DaynoteRepository;
import great.job.mytamin.domain.myday.repository.WishRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.exception.MytaminException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

import static great.job.mytamin.global.exception.ErrorMap.WISH_ALREADY_EXIST_ERROR;
import static great.job.mytamin.global.exception.ErrorMap.WISH_NOT_FOUND_ERROR;

@Service
@RequiredArgsConstructor
public class WishService {

    private final WishRepository wishRepository;
    private final DaynoteRepository daynoteRepository;

    /*
    위시 리스트 조회
    */
    @Transactional(readOnly = true)
    public List<WishResponse> getWishlist(User user) {

        // 정렬 조건 미정
        // publishedList.sort((a, b) -> (int) (a.getOrderId() - b.getOrderId()));

        return entityToDto(wishRepository.findAllByUserAndIsHidden(user, false));
    }

    /*
    위시 수정
    */
    @Transactional
    public void updateWish(User user, Long wishId, WishRequest request) {
        Wish wish = findWishById(user, wishId);
        update(request, wish);
    }

    /*
    위시 삭제
    */
    @Transactional
    public void deleteWish(User user, Long wishId) {
        Wish wish = findWishById(user, wishId);
        delete(wish);
    }

    /*
    위시 생성
    */
    @Transactional
    public WishResponse createWish(User user, String wishText) {
        Wish wish = wishRepository.findByUserAndWishText(user, wishText).orElse(null);
        if (wish != null) restore(wish);
        else wish = save(user, wishText);
        return WishResponse.of(wish, getWishCount(wish));
    }

    /*
    위시 가져오기
    */
    @Transactional
    public Wish findWishById(User user, Long wishId) {
        return wishRepository.findByUserAndWishId(user, wishId)
                .orElseThrow(() -> new MytaminException(WISH_NOT_FOUND_ERROR));
    }

    /*
    위시 전체 삭제
    */
    @Transactional
    public void deleteAll(User user) {
        wishRepository.deleteAllByUser(user);
    }

    private List<WishResponse> entityToDto(List<Wish> wishList) {
        return wishList.stream().map(x -> WishResponse.of(x, getWishCount(x))).collect(Collectors.toList());
    }

    private int getWishCount(Wish wish) {
        return daynoteRepository.countByWish(wish);
    }

    private Wish save(User user, String wishText) {
        Wish wish;
        wish = wishRepository.save(new Wish(
                wishText,
                user
        ));
        return wish;
    }

    private void restore(Wish wish) {
        if (wish.getIsHidden()) {
            wish.updateIsHidden(false); // 숨김 처리된 위시 리스트였다면 복원
            wishRepository.save(wish);
        } else throw new MytaminException(WISH_ALREADY_EXIST_ERROR);
    }

    private void update(WishRequest request, Wish wish) {
        wish.updateWishText(request.getWishText());
        wishRepository.save(wish);
    }

    private void delete(Wish wish) {
        if (getWishCount(wish) != 0) {
            wish.updateIsHidden(true); // 연관된 데이노트가 존재할 경우, 삭제가 아닌 숨김 처리
        } else {
            wishRepository.delete(wish);
        }
    }

}
