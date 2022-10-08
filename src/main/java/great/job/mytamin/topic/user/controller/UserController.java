package great.job.mytamin.topic.user.controller;

import great.job.mytamin.topic.user.dto.response.ProfileResponse;
import great.job.mytamin.topic.user.dto.request.BeMyMsgRequest;
import great.job.mytamin.topic.user.entity.User;
import great.job.mytamin.topic.user.service.UserService;
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

    @GetMapping("/info")
    public String health(@AuthenticationPrincipal User user) {
        return user.getNickname();
    }

    @GetMapping("/profile")
    public ResponseEntity<Object> getProfile(@AuthenticationPrincipal User user) {
        ProfileResponse profileResponse = userService.getProfile(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("프로필 조회", profileResponse));
    }

    @PatchMapping("/nickname/{nickname}")
    public ResponseEntity<Object> updateNickname(@AuthenticationPrincipal User user,
                                                 @PathVariable String nickname) {
        userService.updateNickname(user, nickname);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("닉네임 수정 완료", null));
    }

    @PatchMapping("/bemy-msg")
    public ResponseEntity<Object> updateBeMyMessage(@AuthenticationPrincipal User user,
                                                    @RequestBody BeMyMsgRequest beMyMsgRequest) {
        userService.updateBeMyMessage(user, beMyMsgRequest);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("'되고 싶은 나' 메세지 수정 완료", null));
    }

}
