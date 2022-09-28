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
import great.job.mytamin.global.service.TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static great.job.mytamin.global.exception.ErrorMap.REPORT_ALREADY_DONE;

@Service
@RequiredArgsConstructor
public class ReportService {

    private final TimeService timeService;
    private final MytaminService mytaminService;
    private final ReportRepository reportRepository;

    /*
    하루 진단하기
    */
    @Transactional
    public ReportResponse reportToday(User user, ReportRequest reportRequest) {
        LocalDateTime rawTakeAt = LocalDateTime.now();
        String takeAt = timeService.convertToTakeAt(rawTakeAt);
        Mytamin mytamin = mytaminService.getMytamin(user, takeAt);
        if(mytamin == null) mytamin = mytaminService.createMytamin(user, rawTakeAt);

        if (mytamin.getReport() != null) {
            throw new MytaminException(REPORT_ALREADY_DONE);
        }

        Report report = new Report(
                MentalCondition.getMsgToCode(reportRequest.getMentalConditionCode()),
                reportRequest.getFeelingTag(),
                reportRequest.getTodayReport(),
                mytamin
        );
        Report newReport = reportRepository.save(report);
        mytamin.updateReport(newReport);
        return ReportResponse.of(newReport);
    }

}
