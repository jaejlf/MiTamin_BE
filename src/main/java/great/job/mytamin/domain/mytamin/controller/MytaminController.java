package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import great.job.mytamin.domain.mytamin.dto.response.MytaminResponse;
import great.job.mytamin.domain.mytamin.service.MytaminService;
import great.job.mytamin.domain.user.entity.User;
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
@RequestMapping("/mytamin")
public class MytaminController {

    private final MytaminService mytaminService;

    @PatchMapping("/breath")
    public ResponseEntity<Object> completeBreath(@AuthenticationPrincipal User user) {
        mytaminService.completeBreath(user);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("숨 고르기 완료"));
    }

    @PatchMapping("/sense")
    public ResponseEntity<Object> completeSense(@AuthenticationPrincipal User user) {
        mytaminService.completeSense(user);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("감각 깨우기 완료"));
    }

    @GetMapping("/latest")
    public ResponseEntity<Object> getLatestMytamin(@AuthenticationPrincipal User user) {
        MytaminResponse mytaminResponse = mytaminService.getLatestMytamin(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("최근 섭취한 마이타민", mytaminResponse));
    }

}
