package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.service.UserService;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @GetMapping("/info/nickname")
    public String health(@AuthenticationPrincipal User user) {
        return user.getNickname();
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("마이페이지 프로필 조회", userService.getProfile(user)));
    }

    @GetMapping("/myday")
    public ResponseEntity<Object> getMyday(@AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("이번 달의 마이데이", userService.getMyday(user)));
    }

    @PatchMapping("/nickname/{nickname}")
    public ResponseEntity<Object> editNickname(@AuthenticationPrincipal User user,
                                               @PathVariable String nickname) {
        userService.editNickname(user, nickname);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("닉네임 수정", user.getNickname()));
    }

    @PatchMapping("/bemy/{msg}")
    public ResponseEntity<Object> editBeMyMessage(@AuthenticationPrincipal User user,
                                                  @PathVariable String msg) {
        userService.editBeMyMessage(user, msg);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("'되고 싶은 나' 메세지 수정", user.getBeMyMessage()));
    }

}
