package great.job.mytamin.domain.myday.controller;

import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import great.job.mytamin.domain.myday.dto.request.UpdateWishRequest;
import great.job.mytamin.domain.myday.dto.response.WishlistResponse;
import great.job.mytamin.domain.myday.service.WishService;
import great.job.mytamin.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/wish")
public class WishlistController {

    private final WishService wishService;

    @GetMapping("/list")
    public ResponseEntity<Object> getWishlist(@AuthenticationPrincipal User user) {
        WishlistResponse wishResponseList = wishService.getWishlist(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("위시 리스트 조회", wishResponseList));
    }

    /*
    * (추후) 화면 설계서 변경 사항 확정되면 수정
    * 삭제 시 데이노트 처리 주의
    */
    @PutMapping("/list")
    public ResponseEntity<Object> updateWishlist(@AuthenticationPrincipal User user,
                                                 @RequestBody UpdateWishRequest updateWishRequest) {
        wishService.updateWishlist(user, updateWishRequest.getWishRequest());
        wishService.deleteWishlist(user, updateWishRequest.getDeletedIdList());
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("위시 리스트 업데이트"));
    }

}
