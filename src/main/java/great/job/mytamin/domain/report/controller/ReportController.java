package great.job.mytamin.domain.report.controller;

import great.job.mytamin.domain.report.dto.request.ReportRequest;
import great.job.mytamin.domain.report.dto.response.ReportResponse;
import great.job.mytamin.domain.report.service.ReportService;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

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

}
