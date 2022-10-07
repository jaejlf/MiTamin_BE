package great.job.mytamin.domain.mytamin.service;

import great.job.mytamin.domain.care.dto.response.CareResponse;
import great.job.mytamin.domain.care.entity.Care;
import great.job.mytamin.domain.mytamin.dto.response.MytaminResponse;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.repository.MytaminRepository;
import great.job.mytamin.domain.report.dto.response.ReportResponse;
import great.job.mytamin.domain.report.entity.Report;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.util.ReportUtil;
import great.job.mytamin.global.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MytaminService {

    private final TimeUtil timeUtil;
    private final ReportUtil reportUtil;
    private final MytaminRepository mytaminRepository;

    /*
    최근 섭취한 마이타민
    */
    @Transactional(readOnly = true)
    public MytaminResponse getLatestMytamin(User user) {
        Mytamin mytamin = mytaminRepository.findFirstByUserOrderByMytaminIdDesc(user);
        if (mytamin == null) return null;

        LocalDateTime now = LocalDateTime.now();

        // Report
        Report report = mytamin.getReport();
        ReportResponse reportResponse = null;
        boolean canEditReport = false;
        if (report != null) {
            reportResponse = ReportResponse.of(report, reportUtil.concatFeelingTag(report));
            canEditReport = timeUtil.isInRange(now, report.getCreatedAt(), report.getCreatedAt().plusDays(1));
        }

        // Care
        Care care = mytamin.getCare();
        CareResponse careResponse = null;
        boolean canEditCare = false;
        if (care != null) {
            careResponse = CareResponse.of(care);
            canEditCare = timeUtil.isInRange(now, care.getCreatedAt(), care.getCreatedAt().plusDays(1));
        }
        return MytaminResponse.of(mytamin, canEditReport, canEditCare, reportResponse, careResponse);
    }

    /*
    마이타민 가져오기
    */
    @Transactional(readOnly = true)
    public Mytamin getMytamin(User user, LocalDateTime rawTakeAt) {
        String takeAt = timeUtil.convertToTakeAt(rawTakeAt);
        return mytaminRepository.findByTakeAtAndUser(takeAt, user)
                .orElse(null);
    }

    /*
    마이타민 가져오기 + 없다면 생성
    */
    @Transactional
    public Mytamin getMytaminOrNew(User user) {
        LocalDateTime now = LocalDateTime.now();
        Mytamin mytamin = getMytamin(user, now);
        if (mytamin == null) mytamin = createMytamin(user, now);
        return mytamin;
    }

    /*
    마이타민 생성
    */
    @Transactional
    public Mytamin createMytamin(User user, LocalDateTime rawTakeAt) {
        String takeAt = timeUtil.convertToTakeAt(rawTakeAt);
        Mytamin mytamin = new Mytamin(rawTakeAt, takeAt, user);
        return mytaminRepository.save(mytamin);
    }

}
