package great.job.mytamin.topic.mytamin.service;

import great.job.mytamin.global.util.ReportUtil;
import great.job.mytamin.global.util.TimeUtil;
import great.job.mytamin.topic.mytamin.dto.response.CareResponse;
import great.job.mytamin.topic.mytamin.dto.response.MytaminResponse;
import great.job.mytamin.topic.mytamin.dto.response.ReportResponse;
import great.job.mytamin.topic.mytamin.entity.Care;
import great.job.mytamin.topic.mytamin.entity.Mytamin;
import great.job.mytamin.topic.mytamin.entity.Report;
import great.job.mytamin.topic.mytamin.repository.MytaminRepository;
import great.job.mytamin.topic.user.entity.User;
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
    마이타민 가져오기
    */
    @Transactional(readOnly = true)
    public Mytamin findMytamin(User user, LocalDateTime rawTakeAt) {
        String takeAt = timeUtil.convertToTakeAt(rawTakeAt);
        return mytaminRepository.findByTakeAtAndUser(takeAt, user)
                .orElse(null);
    }

    /*
    마이타민 가져오기 + 없다면 생성
    */
    @Transactional
    public Mytamin findMytaminOrNew(User user) {
        LocalDateTime now = LocalDateTime.now();
        Mytamin mytamin = findMytamin(user, now);
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
