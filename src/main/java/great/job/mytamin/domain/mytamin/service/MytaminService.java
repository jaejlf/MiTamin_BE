package great.job.mytamin.domain.mytamin.service;

import great.job.mytamin.domain.care.entity.Care;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.report.entity.Report;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.care.dto.request.CareRequest;
import great.job.mytamin.domain.report.dto.request.ReportRequest;
import great.job.mytamin.domain.care.dto.response.CareResponse;
import great.job.mytamin.domain.mytamin.dto.response.MytaminResponse;
import great.job.mytamin.domain.report.dto.response.ReportResponse;
import great.job.mytamin.domain.care.enumerate.CareCategory;
import great.job.mytamin.domain.report.enumerate.MentalCondition;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.domain.care.repository.CareRepository;
import great.job.mytamin.domain.mytamin.repository.MytaminRepository;
import great.job.mytamin.domain.report.repository.ReportRepository;
import great.job.mytamin.global.service.TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static great.job.mytamin.global.exception.ErrorMap.CARE_ALREADY_DONE;
import static great.job.mytamin.global.exception.ErrorMap.REPORT_ALREADY_DONE;

@Service
@RequiredArgsConstructor
public class MytaminService {

    private final TimeService timeService;
    private final MytaminRepository mytaminRepository;
    private final ReportRepository reportRepository;
    private final CareRepository careRepository;

    /*
    하루 진단하기
    */
    @Transactional
    public ReportResponse reportToday(User user, ReportRequest reportRequest) {
        Mytamin mytamin = getMytamin(user);
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

    /*
    칭찬 처방하기
    */
    @Transactional
    public CareResponse careToday(User user, CareRequest careRequest) {
        Mytamin mytamin = getMytamin(user);
        if (mytamin.getCare() != null) {
            throw new MytaminException(CARE_ALREADY_DONE);
        }

        Care care = new Care(
                CareCategory.getMsgToCode(careRequest.getCareCategoryCode()),
                careRequest.getCareMsg1(),
                careRequest.getCareMsg2(),
                mytamin
        );

        Care newCare = careRepository.save(care);
        mytamin.updateCare(newCare);
        return CareResponse.of(newCare);
    }

    /*
    최근 마이타민
    */
    @Transactional(readOnly = true)
    public MytaminResponse getLatestMytamin(User user) {
        Mytamin mytamin = mytaminRepository.findFirstByUserOrderByMytaminIdDesc(user);
        if (mytamin == null) return null;

        LocalDateTime rawTakeAt = mytamin.getRawTakeAt();
        boolean canEdit = timeService.isCreatedAtWithin24(rawTakeAt);

        if (mytamin.getReport() != null) {
            int memtalConditionCode = MentalCondition.getCodeToMsg(mytamin.getReport().getMentalCondition());
            if (mytamin.getCare() != null) {
                return MytaminResponse.of(mytamin, memtalConditionCode, canEdit);
            } else {
                return MytaminResponse.withReport(mytamin, memtalConditionCode, canEdit);
            }
        } else {
            return MytaminResponse.withCare(mytamin, canEdit);
        }
    }

    // 오늘의 마이타민 가져오기
    // LocalDateTime.now -> 커스텀 date로 변경의 여지가 있다.
    public Mytamin getMytamin(User user) {
        LocalDateTime rawTakeAt = LocalDateTime.now();
        String takeAt = timeService.convertTimeFormat(rawTakeAt);
        Mytamin mytamin = mytaminRepository.findByTakeAtAndUser(takeAt, user);

        // 존재하는 마이타민이 없다면 새로 생성
        if (mytamin == null) {
            mytamin = new Mytamin(rawTakeAt, takeAt, user);
            mytaminRepository.save(mytamin);
        }
        return mytamin;
    }

}
