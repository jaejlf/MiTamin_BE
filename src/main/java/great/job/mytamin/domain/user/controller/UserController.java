package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.service.UserService;
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

}
