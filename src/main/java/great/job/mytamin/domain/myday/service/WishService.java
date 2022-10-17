package great.job.mytamin.domain.myday.service;

import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.domain.myday.dto.request.WishRequest;
import great.job.mytamin.domain.myday.dto.response.WishResponse;
import great.job.mytamin.domain.myday.dto.response.WishlistResponse;
import great.job.mytamin.domain.myday.entity.Wish;
import great.job.mytamin.domain.myday.repository.DaynoteRepository;
import great.job.mytamin.domain.myday.repository.WishRepository;
import great.job.mytamin.domain.user.entity.User;
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
    public WishlistResponse getWishlist(User user) {
        List<Wish> publishedList = wishRepository.findByUserAndIsHidden(user, false);
        List<Wish> hiddenList = wishRepository.findByUserAndIsHidden(user, true);

        publishedList.sort((a, b) -> (int) (a.getOrderId() - b.getOrderId()));
        hiddenList.sort((a, b) -> (int) (a.getOrderId() - b.getOrderId()));

        return WishlistResponse.of(entityToDto(publishedList), entityToDto(hiddenList));
    }

    /*
    위시 리스트 수정
    */
    @Transactional
    public void updateWishlist(User user, List<WishRequest> wishRequestList) {
        for (WishRequest wishRequest : wishRequestList) {
            if (wishRequest.getWishId() == null) createWish(wishRequest, user);
            else {
                Wish wish = findWishById(wishRequest.getWishId());
                hasAuthorized(wish, user);
                updateWish(wish, wishRequest);
            }
        }
    }

    /*
    위시 리스트 삭제
    */
    @Transactional
    public void deleteWishlist(User user, List<Long> deleteIdList) {
        for (Long wishId : deleteIdList) {
            Wish wish = findWishById(wishId);
            hasAuthorized(wish, user);
            wishRepository.delete(wish);
        }
    }

    /*
    위시 생성
    */
    @Transactional
    public Wish createWish(WishRequest wishRequest, User user) {
        checkExistence(user, wishRequest.getWishText());
        Wish wish = new Wish(
                wishRequest.getWishText(),
                wishRequest.getIsHidden(),
                wishRequest.getOrderId(),
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
            WishRequest wishRequest = new WishRequest(
                    null,
                    wishText,
                    false,
                    999
            );
            return createWish(wishRequest, user);
        }
        return wish.get();
    }

    private void updateWish(Wish wish, WishRequest wishRequest) {
        String text = wishRequest.getWishText();
        boolean isHidden = wishRequest.getIsHidden();
        int orderId = wishRequest.getOrderId();
        wish.updateWish(text, isHidden, orderId);
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
