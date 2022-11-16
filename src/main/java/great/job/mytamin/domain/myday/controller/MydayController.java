package great.job.mytamin.domain.myday.controller;

import great.job.mytamin.domain.myday.dto.response.MydayResponse;
import great.job.mytamin.domain.myday.service.MydayService;
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
@RequestMapping("/myday")
public class MydayController {

    private final MydayService mydayService;

    @GetMapping("/info")
    public ResponseEntity<Object> getMyday(@AuthenticationPrincipal User user) {
        MydayResponse result = mydayService.getMyday(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("이번 달의 마이데이", result));
    }

}
