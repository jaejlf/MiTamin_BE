package great.job.mytamin.domain.mytamin.service;

import great.job.mytamin.domain.mytamin.dto.response.CareResponse;
import great.job.mytamin.domain.mytamin.dto.response.MytaminResponse;
import great.job.mytamin.domain.mytamin.dto.response.ReportResponse;
import great.job.mytamin.domain.mytamin.entity.Care;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.entity.Report;
import great.job.mytamin.domain.mytamin.repository.MytaminRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.util.ReportUtil;
import great.job.mytamin.domain.util.TimeUtil;
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
    숨 고르기 완료
    */
    @Transactional
    public void completeBreath(User user) {
        user.updateBreathTime();
    }

    /*
    감각 깨우기 완료
    */
    @Transactional
    public void completeSense(User user) {
        user.updateSenseTime();
    }

    /*
    최근 섭취한 마이타민
    */
    @Transactional(readOnly = true)
    public MytaminResponse getLatestMytamin(User user) {
        Mytamin mytamin = mytaminRepository.findFirstByUserOrderByMytaminIdDesc(user);
        if (mytamin == null) return null;

        // Report
        Report report = mytamin.getReport();
        ReportResponse reportResponse = null;
        if (report != null) {
            reportResponse = ReportResponse.of(
                    report,
                    reportUtil.concatFeelingTag(report),
                    timeUtil.canEditReport(report));
        }

        // Care
        Care care = mytamin.getCare();
        CareResponse careResponse = null;
        if (care != null) {
            careResponse = CareResponse.of(care, timeUtil.canEditCare(care));
        }

        return MytaminResponse.of(mytamin, reportResponse, careResponse);
    }

    /*
    마이타민 가져오기 + 없다면 생성
    */
    @Transactional
    public Mytamin findMytaminOrNew(User user) {
        Mytamin mytamin = findMytamin(user, LocalDateTime.now());
        if (mytamin == null) mytamin = createMytamin(user);
        return mytamin;
    }

    /*
    마이타민 가져오기
    */
    @Transactional(readOnly = true)
    public Mytamin findMytamin(User user, LocalDateTime target) {
        LocalDateTime takeAt = timeUtil.convertToMytaminDate(target);
        return mytaminRepository.findByUserAndTakeAt(user, takeAt)
                .orElse(null);
    }

    /*
    마이타민 생성
    */
    @Transactional
    public Mytamin createMytamin(User user) {
        Mytamin mytamin = new Mytamin(timeUtil.convertToMytaminDate(LocalDateTime.now()), user);
        return mytaminRepository.save(mytamin);
    }

}
