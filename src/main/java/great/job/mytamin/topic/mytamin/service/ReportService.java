package great.job.mytamin.topic.mytamin.service;

import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.topic.util.ReportUtil;
import great.job.mytamin.topic.util.TimeUtil;
import great.job.mytamin.topic.mytamin.dto.request.ReportRequest;
import great.job.mytamin.topic.mytamin.dto.response.ReportResponse;
import great.job.mytamin.topic.mytamin.entity.Mytamin;
import great.job.mytamin.topic.mytamin.entity.Report;
import great.job.mytamin.topic.mytamin.enumerate.MentalCondition;
import great.job.mytamin.topic.mytamin.repository.ReportRepository;
import great.job.mytamin.topic.user.entity.User;
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
        Mytamin mytamin = mytaminService.findMytaminOrNew(user);
        if (mytamin.getReport() != null) throw new MytaminException(REPORT_ALREADY_DONE_ERROR);
        Report newReport = saveNewReport(reportRequest, mytamin);
        return ReportResponse.of(
                newReport,
                reportUtil.concatFeelingTag(newReport),
                timeUtil.canEditReport(newReport));
    }

    /*
    하루 진단 수정
    */
    @Transactional
    public void updateReport(User user, Long reportId, ReportRequest reportRequest) {
        Report report = findReportById(reportId);
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

    /*
    하루 진단 조회
    */
    public ReportResponse getReport(Long reportId) {
        Report report = findReportById(reportId);
        return ReportResponse.of(
                report,
                reportUtil.concatFeelingTag(report),
                timeUtil.canEditReport(report));
    }

    private Report findReportById(Long reportId) {
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
