package great.job.mytamin.domain.myday.controller;

import great.job.mytamin.domain.myday.dto.request.WishRequest;
import great.job.mytamin.domain.myday.dto.response.WishResponse;
import great.job.mytamin.domain.myday.service.WishService;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishlistController {

    private final WishService wishService;

    @GetMapping("/list")
    public ResponseEntity<Object> getWishlist(@AuthenticationPrincipal User user) {
        List<WishResponse> wishResponseList = wishService.getWishlist(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("위시 리스트 조회", wishResponseList));
    }

    @PostMapping("/new")
    public ResponseEntity<Object> createWish(@AuthenticationPrincipal User user,
                                             @RequestBody WishRequest wishRequest) {
        wishService.createWish(user, wishRequest.getWishText());
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("위시 생성"));
    }

    @PutMapping("/{wishId}")
    public ResponseEntity<Object> updateWish(@AuthenticationPrincipal User user,
                                             @PathVariable Long wishId,
                                             @RequestBody WishRequest wishRequest) {
        wishService.updateWish(user, wishId, wishRequest);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("위시 수정"));
    }

    @DeleteMapping("/{wishId}")
    public ResponseEntity<Object> deleteWish(@AuthenticationPrincipal User user,
                                             @PathVariable Long wishId) {
        wishService.deleteWish(user, wishId);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("위시 삭제"));
    }

}
