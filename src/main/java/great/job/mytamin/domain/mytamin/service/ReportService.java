package great.job.mytamin.domain.mytamin.service;

import great.job.mytamin.domain.mytamin.dto.request.ReportRequest;
import great.job.mytamin.domain.mytamin.dto.response.ReportResponse;
import great.job.mytamin.domain.mytamin.dto.response.WeeklyMentalResponse;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.entity.Report;
import great.job.mytamin.domain.mytamin.repository.ReportRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.util.ReportUtil;
import great.job.mytamin.domain.util.TimeUtil;
import great.job.mytamin.global.exception.MytaminException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static great.job.mytamin.domain.mytamin.enumerate.MentalCondition.validateCode;
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
        Report newReport = saveNewReport(user, reportRequest, mytamin);
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
                validateCode(reportRequest.getMentalConditionCode()),
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
    @Transactional(readOnly = true)
    public ReportResponse getReport(Long reportId) {
        Report report = findReportById(reportId);
        return ReportResponse.of(
                report,
                reportUtil.concatFeelingTag(report),
                timeUtil.canEditReport(report));
    }

    /*
    주간 마음 컨디션
    */
    @Transactional(readOnly = true)
    public List<WeeklyMentalResponse> getWeeklyMentalReport(User user) {
        LocalDateTime start = LocalDateTime.now().minusDays(7); // 오늘을 기준으로 7일 전
        List<WeeklyMentalResponse> weeklyMentalResponseList = new ArrayList<>();

        for (int i = 0; i < 8; i++) {
            LocalDateTime target = start.plusDays(i);
            Mytamin mytamin = mytaminService.findMytamin(user, target);

            weeklyMentalResponseList.add(WeeklyMentalResponse.of(
                            i == 7 ? "오늘" : timeUtil.convertDayNumToStr(target.getDayOfWeek().getValue()),
                            mytamin != null && mytamin.getReport() != null ? mytamin.getReport().getMentalConditionCode() : 0
                    )
            );
        }

        return weeklyMentalResponseList;
    }

    private Report findReportById(Long reportId) {
        return reportRepository.findById(reportId)
                .orElseThrow(() -> new MytaminException(REPORT_NOT_FOUND_ERROR));
    }

    private void hasAuthorized(Report report, User user) {
        if (!report.getUser().equals(user)) {
            throw new MytaminException(NO_AUTH_ERROR);
        }
    }

    private void canEdit(Report report) {
        if (!timeUtil.isInRange(LocalDateTime.now(), report.getCreatedAt(), report.getCreatedAt().plusDays(1))) {
            throw new MytaminException(EDIT_TIMEOUT_ERROR);
        }
    }

    private Report saveNewReport(User user, ReportRequest reportRequest, Mytamin mytamin) {
        Report report = new Report(
                user,
                validateCode(reportRequest.getMentalConditionCode()),
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
