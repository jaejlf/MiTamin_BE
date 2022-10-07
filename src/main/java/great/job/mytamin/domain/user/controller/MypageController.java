package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.dto.response.MydayResponse;
import great.job.mytamin.domain.user.dto.response.ProfileResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.service.MypageService;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MypageController {

    private final MypageService mypageService;

    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile(@AuthenticationPrincipal User user) {
        ProfileResponse profileResponse = mypageService.getProfile(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("마이페이지 프로필 조회", profileResponse));
    }

    @GetMapping("/myday")
    public ResponseEntity<Object> getMyday(@AuthenticationPrincipal User user) {
        MydayResponse mydayResponse = mypageService.getMyday(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("이번 달의 마이데이", mydayResponse));
    }

}
