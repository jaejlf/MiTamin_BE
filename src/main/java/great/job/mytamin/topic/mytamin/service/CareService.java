package great.job.mytamin.topic.mytamin.service;

import great.job.mytamin.topic.mytamin.dto.request.CareRequest;
import great.job.mytamin.topic.mytamin.dto.response.CareResponse;
import great.job.mytamin.topic.mytamin.entity.Care;
import great.job.mytamin.topic.mytamin.entity.Mytamin;
import great.job.mytamin.topic.mytamin.enumerate.CareCategory;
import great.job.mytamin.topic.mytamin.repository.CareRepository;
import great.job.mytamin.topic.user.entity.User;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import static great.job.mytamin.global.exception.ErrorMap.*;

@Service
@RequiredArgsConstructor
public class CareService {

    private final TimeUtil timeUtil;
    private final MytaminService mytaminService;
    private final CareRepository careRepository;

    /*
    칭찬 처방하기
    */
    @Transactional
    public CareResponse createCare(User user, CareRequest careRequest) {
        Mytamin mytamin = mytaminService.getMytaminOrNew(user);
        if (mytamin.getCare() != null) throw new MytaminException(CARE_ALREADY_DONE);
        return CareResponse.of(saveNewCare(careRequest, mytamin));
    }

    /*
    칭찬 처방 수정
    */
    @Transactional
    public void updateCare(User user, Long careId, CareRequest careRequest) {
        Care care = getCare(careId);
        hasAuthorized(care, user);
        canEdit(care);

        care.updateAll(
                CareCategory.getMsgToCode(careRequest.getCareCategoryCode()),
                careRequest.getCareMsg1(),
                careRequest.getCareMsg2()
        );
        careRepository.save(care);
    }

    private Care getCare(Long careId) {
        return careRepository.findById(careId)
                .orElseThrow(() -> new MytaminException(CARE_NOT_FOUND_ERROR));
    }

    private void hasAuthorized(Care care, User user) {
        if (!care.getMytamin().getUser().equals(user)) {
            throw new MytaminException(NO_AUTH_ERROR);
        }
    }

    private void canEdit(Care care) {
        if (!timeUtil.isInRange(LocalDateTime.now(), care.getCreatedAt(), care.getCreatedAt().plusDays(1))) {
            throw new MytaminException(EDIT_TIMEOUT_ERROR);
        }
    }

    private Care saveNewCare(CareRequest careRequest, Mytamin mytamin) {
        Care care = new Care(
                CareCategory.getMsgToCode(careRequest.getCareCategoryCode()),
                careRequest.getCareMsg1(),
                careRequest.getCareMsg2(),
                mytamin
        );
        Care newCare = careRepository.save(care);
        mytamin.updateCare(newCare);
        return newCare;
    }

}
