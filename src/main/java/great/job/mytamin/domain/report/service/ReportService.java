package great.job.mytamin.domain.report.service;

import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.service.MytaminService;
import great.job.mytamin.domain.report.dto.request.ReportRequest;
import great.job.mytamin.domain.report.dto.response.ReportResponse;
import great.job.mytamin.domain.report.entity.Report;
import great.job.mytamin.domain.report.enumerate.MentalCondition;
import great.job.mytamin.domain.report.repository.ReportRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.util.ReportUtil;
import great.job.mytamin.global.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static great.job.mytamin.global.exception.ErrorMap.*;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TimeUtil timeUtil;
    private final ReportUtil reportUtil;
    private final MytaminService mytaminService;
    private final ReportRepository reportRepository;

    /*
    하루 진단하기
    */
    @Transactional
    public ReportResponse createReport(User user, ReportRequest reportRequest) {
        Mytamin mytamin = mytaminService.getMytaminOrNew(user);
        if (mytamin.getReport() != null) throw new MytaminException(REPORT_ALREADY_DONE);

        Report newReport = saveNewReport(reportRequest, mytamin);
        return ReportResponse.of(newReport, reportUtil.concatFeelingTag(newReport));
    }

    /*
    하루 진단 수정
    */
    @Transactional
    public void updateReport(User user, Long reportId, ReportRequest reportRequest) {
        Report report = getReport(reportId);
        hasAuthorized(report, user);
        canEdit(report);

        report.updateAll(
                MentalCondition.convertCodeToMsg(reportRequest.getMentalConditionCode()),
                reportRequest.getTag1(),
                reportRequest.getTag2(),
                reportRequest.getTag3(),
                reportRequest.getTodayReport()
        );
        reportRepository.save(report);
    }

    private Report getReport(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new MytaminException(REPORT_NOT_FOUND_ERROR));
    }

    private void hasAuthorized(Report report, User user) {
        if (!report.getMytamin().getUser().equals(user)) {
            throw new MytaminException(NO_AUTH_ERROR);
        }
    }

    private void canEdit(Report report) {
        if (!timeUtil.isInRange(LocalDateTime.now(), report.getCreatedAt(), report.getCreatedAt().plusDays(1))) {
            throw new MytaminException(EDIT_TIMEOUT_ERROR);
        }
    }

    private Report saveNewReport(ReportRequest reportRequest, Mytamin mytamin) {
        Report report = new Report(
                MentalCondition.convertCodeToMsg(reportRequest.getMentalConditionCode()),
                reportRequest.getTag1(),
                reportRequest.getTag2(),
                reportRequest.getTag3(),
                reportRequest.getTodayReport(),
                mytamin
        );
        Report newReport = reportRepository.save(report);
        mytamin.updateReport(newReport);
        return newReport;
    }

}
