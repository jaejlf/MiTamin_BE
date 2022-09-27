package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.service.MytaminService;
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
@RequestMapping("/mytamin")
public class MytaminController {

    private final MytaminService mytaminService;

    @GetMapping("/latest")
    public ResponseEntity<Object> getLatestMytamin(@AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("최근 마이타민", mytaminService.getLatestMytamin(user)));
    }

}
