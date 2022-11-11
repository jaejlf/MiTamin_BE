package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.service.HomeService;
import great.job.mytamin.domain.user.dto.response.ActiveResponse;
import great.job.mytamin.domain.user.dto.response.WelcomeResponse;
import great.job.mytamin.domain.user.entity.User;
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
@RequestMapping("/home")
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/welcome")
    public ResponseEntity<Object> welcome(@AuthenticationPrincipal User user) {
        WelcomeResponse result = homeService.welcome(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("웰컴 메세지", result));
    }

    @GetMapping("/progress/status")
    public ResponseEntity<Object> getProgressStatus(@AuthenticationPrincipal User user) {
        ActiveResponse result = homeService.getProgressStatus(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("행동 완료 상태", result));
    }

}
