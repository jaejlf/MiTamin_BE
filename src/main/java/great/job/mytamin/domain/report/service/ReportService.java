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
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static great.job.mytamin.global.exception.ErrorMap.REPORT_ALREADY_DONE;

@Service
@RequiredArgsConstructor
public class ReportService {

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
