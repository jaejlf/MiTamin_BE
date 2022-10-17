package great.job.mytamin.domain.mytamin.service;

import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.domain.util.TimeUtil;
import great.job.mytamin.domain.mytamin.dto.request.CareRequest;
import great.job.mytamin.domain.mytamin.dto.response.CareResponse;
import great.job.mytamin.domain.mytamin.entity.Care;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.enumerate.CareCategory;
import great.job.mytamin.domain.mytamin.repository.CareRepository;
import great.job.mytamin.domain.user.entity.User;
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
        Mytamin mytamin = mytaminService.findMytaminOrNew(user);
        if (mytamin.getCare() != null) throw new MytaminException(CARE_ALREADY_DONE_ERROR);
        Care newCare = saveNewCare(careRequest, mytamin);
        return CareResponse.of(newCare, timeUtil.canEditCare(newCare));
    }

    /*
    칭찬 처방 수정
    */
    @Transactional
    public void updateCare(User user, Long careId, CareRequest careRequest) {
        Care care = findCareById(careId);
        hasAuthorized(care, user);
        canEdit(care);

        care.updateAll(
                CareCategory.getMsgToCode(careRequest.getCareCategoryCode()),
                careRequest.getCareMsg1(),
                careRequest.getCareMsg2()
        );
        careRepository.save(care);
    }

    /*
    칭찬 처방 조회
    */
    @Transactional(readOnly = true)
    public CareResponse getCare(Long careId) {
        Care care = findCareById(careId);
        return CareResponse.of(care, timeUtil.canEditCare(care));
    }

    private Care findCareById(Long careId) {
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
