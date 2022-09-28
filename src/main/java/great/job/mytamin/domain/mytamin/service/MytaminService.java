package great.job.mytamin.domain.mytamin.service;

import great.job.mytamin.domain.mytamin.dto.response.MytaminResponse;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.repository.MytaminRepository;
import great.job.mytamin.domain.report.enumerate.MentalCondition;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.service.TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class MytaminService {

    private final TimeService timeService;
    private final MytaminRepository mytaminRepository;

    /*
    최근 섭취한 마이타민
    */
    @Transactional(readOnly = true)
    public MytaminResponse getLatestMytamin(User user) {
        Mytamin mytamin = mytaminRepository.findFirstByUserOrderByMytaminIdDesc(user);
        if (mytamin == null) return null;

        // 수정 가능 여부
        // -> 24시간 이내 생성된 마이타민일 경우 수정 가능
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime rawTakeAt = mytamin.getRawTakeAt();
        boolean canEdit = timeService.isInRange(rawTakeAt, now.minusDays(1), now);

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

    /*
    마이타민 가져오기
    */
    @Transactional(readOnly = true)
    public Mytamin getMytamin(User user, String takeAt) {
        return mytaminRepository.findByTakeAtAndUser(takeAt, user)
                .orElse(null);
    }

    /*
    마이타민 생성
    */
    @Transactional
    public Mytamin createMytamin(User user, LocalDateTime rawTakeAt) {
        String takeAt = timeService.convertToTakeAt(rawTakeAt);
        Mytamin mytamin = new Mytamin(rawTakeAt, takeAt, user);
        return mytaminRepository.save(mytamin);
    }

}
