package great.job.mytamin.domain.user.controller;

import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import great.job.mytamin.domain.user.dto.request.BeMyMsgRequest;
import great.job.mytamin.domain.user.dto.response.ProfileResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    @PatchMapping("/img")
    public ResponseEntity<Object> updateProfileImg(@AuthenticationPrincipal User user,
                                                   @RequestPart("file") MultipartFile file) {
        userService.updateProfileImg(user, file);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("프로필 이미지 수정 완료"));
    }

    @PatchMapping("/nickname/{nickname}")
    public ResponseEntity<Object> updateNickname(@AuthenticationPrincipal User user,
                                                 @PathVariable String nickname) {
        userService.updateNickname(user, nickname);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("닉네임 수정 완료"));
    }

    @PatchMapping("/bemy-msg")
    public ResponseEntity<Object> updateBeMyMessage(@AuthenticationPrincipal User user,
                                                    @RequestBody BeMyMsgRequest beMyMsgRequest) {
        userService.updateBeMyMessage(user, beMyMsgRequest);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("'되고 싶은 나' 메세지 수정 완료"));
    }

}
