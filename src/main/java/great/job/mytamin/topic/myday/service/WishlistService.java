package great.job.mytamin.topic.myday.service;

import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.topic.myday.dto.request.WishRequest;
import great.job.mytamin.topic.myday.dto.response.WishResponse;
import great.job.mytamin.topic.myday.dto.response.WishlistResponse;
import great.job.mytamin.topic.myday.entity.Wish;
import great.job.mytamin.topic.myday.repository.DaynoteRepository;
import great.job.mytamin.topic.myday.repository.WishRepository;
import great.job.mytamin.topic.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static great.job.mytamin.global.exception.ErrorMap.NO_AUTH_ERROR;
import static great.job.mytamin.global.exception.ErrorMap.WISH_NOT_FOUND_ERROR;

@Service
@RequiredArgsConstructor
public class WishlistService {

    private final WishRepository wishRepository;
    private final DaynoteRepository daynoteRepository;

    /*
    위시 리스트 조회
    */
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
    public void deleteWishlist(User user, List<Long> deleteIdList) {
        for (Long wishId : deleteIdList) {
            Wish wish = findWishById(wishId);
            hasAuthorized(wish, user);
            wishRepository.delete(wish);
        }
    }

    private void createWish(WishRequest wishRequest, User user) {
        Wish wish = new Wish(
                wishRequest.getText(),
                wishRequest.getIsHidden(),
                wishRequest.getOrderId(),
                user
        );
        wishRepository.save(wish);
    }

    private void updateWish(Wish wish, WishRequest wishRequest) {
        String text = wishRequest.getText();
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

    private List<WishResponse> entityToDto(List<Wish> wishList) {
        return wishList.stream().map(x -> WishResponse.of(x, getWishCount(x))).collect(Collectors.toList());
    }

    private int getWishCount(Wish wish) {
        return daynoteRepository.countByWish(wish);
    }

}
