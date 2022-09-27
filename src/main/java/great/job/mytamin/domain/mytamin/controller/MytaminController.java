package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.care.dto.request.CareRequest;
import great.job.mytamin.domain.report.dto.request.ReportRequest;
import great.job.mytamin.global.dto.response.ResultResponse;
import great.job.mytamin.domain.mytamin.service.MytaminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/mytamin")
public class MytaminController {

    private final MytaminService mytaminService;

    @PostMapping("/report")
    public ResponseEntity<Object> reportToday(@AuthenticationPrincipal User user,
                                              @RequestBody ReportRequest reportRequest) {
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("하루 진단하기", mytaminService.reportToday(user, reportRequest)));
    }

    @PostMapping("/care")
    public ResponseEntity<Object> careToday(@AuthenticationPrincipal User user,
                                            @RequestBody CareRequest careRequest) {
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("칭찬 처방하기", mytaminService.careToday(user, careRequest)));
    }

    @GetMapping("/latest")
    public ResponseEntity<Object> getLatestMytamin(@AuthenticationPrincipal User user) {
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("최근 마이타민", mytaminService.getLatestMytamin(user)));
    }

}
