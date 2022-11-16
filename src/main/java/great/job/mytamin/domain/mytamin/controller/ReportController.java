package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.dto.request.ReportRequest;
import great.job.mytamin.domain.mytamin.dto.response.FeelingRankResponse;
import great.job.mytamin.domain.mytamin.dto.response.ReportResponse;
import great.job.mytamin.domain.mytamin.dto.response.WeeklyMentalConditionResponse;
import great.job.mytamin.domain.mytamin.service.ReportService;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/report")
public class ReportController {

    private final ReportService reportService;

    @PostMapping("/new")
    public ResponseEntity<Object> createReport(@AuthenticationPrincipal User user,
                                               @RequestBody @Valid ReportRequest request) {
        ReportResponse result = reportService.createReport(user, request);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("하루 진단", result));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<Object> getReport(@AuthenticationPrincipal User user,
                                            @PathVariable Long reportId) {
        ReportResponse result = reportService.getReport(user, reportId);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok(reportId + "번 하루 진단 조회", result));
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<Object> updateReport(@AuthenticationPrincipal User user,
                                               @PathVariable Long reportId,
                                               @RequestBody @Valid ReportRequest request) {
        reportService.updateReport(user, reportId, request);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok(reportId + "번 하루 진단 수정"));
    }

    @GetMapping("/weekly/mental")
    public ResponseEntity<Object> getWeeklyMentalReport(@AuthenticationPrincipal User user) {
        List<WeeklyMentalConditionResponse> result = reportService.getWeeklyMentalReport(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("주간 마음 컨디션 조회", result));
    }

    @GetMapping("/feeling/rank")
    public ResponseEntity<Object> getMonthlyFeelingRank(@AuthenticationPrincipal User user) {
        List<FeelingRankResponse> result = reportService.getMonthlyFeelingRank(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("이번 달 감정 TOP3", result));
    }

}
