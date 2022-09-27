package great.job.mytamin.domain.home.controller;

import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.dto.response.ResultResponse;
import great.job.mytamin.domain.home.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
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
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("웰컴 메세지", homeService.welcome(user)));
    }

    @GetMapping("/progress/status")
    public ResponseEntity<Object> getProgressStatus(@AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("행동 완료 상태", homeService.getProgressStatus(user)));
    }

    @PatchMapping("/breath")
    public ResponseEntity<Object> completeBreath(@AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("숨 고르기 완료", homeService.completeBreath(user)));
    }

    @PatchMapping("/sense")
    public ResponseEntity<Object> completeSense(@AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("감각 깨우기 완료", homeService.completeSense(user)));
    }

}
