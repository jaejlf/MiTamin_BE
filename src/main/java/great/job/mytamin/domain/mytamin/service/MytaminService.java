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
    @Transactional
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
