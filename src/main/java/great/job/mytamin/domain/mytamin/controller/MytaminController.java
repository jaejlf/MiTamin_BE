package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.dto.response.MonthlyMytaminResponse;
import great.job.mytamin.domain.mytamin.dto.response.MytaminResponse;
import great.job.mytamin.domain.mytamin.dto.response.WeeklyMytaminResponse;
import great.job.mytamin.domain.mytamin.service.MytaminService;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
        MytaminResponse result = mytaminService.getLatestMytamin(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("최근 섭취한 마이타민", result));
    }

    @GetMapping("/monthly/{date}")
    public ResponseEntity<Object> getMonthlyMytamin(@AuthenticationPrincipal User user,
                                                    @PathVariable String date) {
        List<MonthlyMytaminResponse> result = mytaminService.getMonthlyMytamin(user, date);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("월간 마이타민 기록 조회", result));
    }

    @GetMapping("/weekly/{date}")
    public ResponseEntity<Object> getWeeklyMytamin(@AuthenticationPrincipal User user,
                                                   @PathVariable String date) {
        Map<Integer, WeeklyMytaminResponse> result = mytaminService.getWeeklyMytamin(user, date);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("주간 마이타민 기록 조회", result));
    }

    @DeleteMapping("/{mytaminId}")
    public ResponseEntity<Object> deleteMytamin(@AuthenticationPrincipal User user,
                                                @PathVariable Long mytaminId) {
        mytaminService.deleteMytamin(user, mytaminId);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok(mytaminId + "번 마이타민 삭제"));
    }

}
