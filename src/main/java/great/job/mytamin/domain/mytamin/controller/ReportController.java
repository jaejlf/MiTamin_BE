package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.dto.request.ReportRequest;
import great.job.mytamin.domain.mytamin.dto.response.FeelingRankResponse;
import great.job.mytamin.domain.mytamin.dto.response.ReportResponse;
import great.job.mytamin.domain.mytamin.dto.response.WeeklyMentalReportResponse;
import great.job.mytamin.domain.mytamin.service.ReportService;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
                                               @RequestBody ReportRequest reportRequest) {
        ReportResponse reportResponse = reportService.createReport(user, reportRequest);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("하루 진단하기", reportResponse));
    }

    @GetMapping("/{reportId}")
    public ResponseEntity<Object> getReport(@PathVariable Long reportId) {
        ReportResponse reportResponse = reportService.getReport(reportId);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("하루 진단 조회", reportResponse));
    }

    @PutMapping("/{reportId}")
    public ResponseEntity<Object> updateReport(@AuthenticationPrincipal User user,
                                               @PathVariable Long reportId,
                                               @RequestBody ReportRequest reportRequest) {
        reportService.updateReport(user, reportId, reportRequest);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("하루 진단 수정 완료"));
    }

    @GetMapping("/weekly/mental")
    public ResponseEntity<Object> getWeeklyMentalReport(@AuthenticationPrincipal User user) {
        List<WeeklyMentalReportResponse> weeklyMentalReportResponseList = reportService.getWeeklyMentalReport(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("주간 마음 컨디션", weeklyMentalReportResponseList));
    }

    @GetMapping("/feeling/rank")
    public ResponseEntity<Object> getMonthlyFeelingRank(@AuthenticationPrincipal User user) {
        List<FeelingRankResponse> feelingRankResponseList = reportService.getMonthlyFeelingRank(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("이번 달 가장 많이 느낀 감정", feelingRankResponseList));
    }

}
