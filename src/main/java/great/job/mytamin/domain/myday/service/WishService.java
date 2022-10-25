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
import java.util.Optional;
import java.util.stream.Collectors;

import static great.job.mytamin.global.exception.ErrorMap.*;

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
    public void updateWish(User user, Long wishId, WishRequest wishRequest) {
        Wish wish = findWishById(wishId);
        hasAuthorized(wish, user);
        wish.updateWishText(wishRequest.getWishText());
        wishRepository.save(wish);
    }

    /*
    위시 삭제
    */
    @Transactional
    public void deleteWish(User user, Long wishId) {
        Wish wish = findWishById(wishId);
        hasAuthorized(wish, user);
        if (getWishCount(wish) != 0) {
            wish.updateIsHiddenTrue();
        } else {
            wishRepository.delete(wish);
        }
    }

    /*
    위시 생성
    */
    @Transactional
    public Wish createWish(User user, String wishText) {
        Wish wish = new Wish(
                wishText,
                user
        );
        return wishRepository.save(wish);
    }

    /*
    위시 가져오기 + 없다면 생성
    */
    @Transactional
    public Wish findWishOrElseNew(User user, String wishText) {
        Optional<Wish> wish = wishRepository.findByUserAndWishText(user, wishText);
        if (wish.isEmpty()) {
            return createWish(user, wishText);
        }
        return wish.get();
    }

    /*
    위시 전체 삭제
    */
    @Transactional
    public void deleteAll(User user) {
        wishRepository.deleteAllByUser(user);
    }

    private void hasAuthorized(Wish wish, User user) {
        if (!wish.getUser().equals(user)) {
            throw new MytaminException(NO_AUTH_ERROR);
        }
    }

    private Wish findWishById(Long wishId) {
        return wishRepository.findById(wishId)
                .orElseThrow(() -> new MytaminException(WISH_NOT_FOUND_ERROR));
    }

    private void checkExistence(User user, String wishText) {
        if (wishRepository.findByUserAndWishText(user, wishText).isPresent()) {
            throw new MytaminException(WISH_ALREADY_EXIST_ERROR);
        }
    }

    private List<WishResponse> entityToDto(List<Wish> wishList) {
        return wishList.stream().map(x -> WishResponse.of(x, getWishCount(x))).collect(Collectors.toList());
    }

    private int getWishCount(Wish wish) {
        return daynoteRepository.countByWish(wish);
    }

}
