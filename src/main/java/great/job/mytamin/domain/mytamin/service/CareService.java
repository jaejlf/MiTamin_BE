package great.job.mytamin.domain.mytamin.service;

import great.job.mytamin.domain.mytamin.dto.request.CareRequest;
import great.job.mytamin.domain.mytamin.dto.request.CareSearchFilter;
import great.job.mytamin.domain.mytamin.dto.response.CareHistoryResponse;
import great.job.mytamin.domain.mytamin.dto.response.CareResponse;
import great.job.mytamin.domain.mytamin.dto.response.RandomCareResponse;
import great.job.mytamin.domain.mytamin.entity.Care;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.repository.CareRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.util.TimeUtil;
import great.job.mytamin.global.exception.MytaminException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static great.job.mytamin.domain.mytamin.enumerate.CareCategory.validateCode;
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
    public CareResponse createCare(User user, CareRequest request) {
        Mytamin mytamin = mytaminService.findMytaminOrNew(user);
        if (mytamin.getCare() != null) throw new MytaminException(CARE_ALREADY_DONE_ERROR);

        Care care = save(user, request, mytamin);
        return CareResponse.of(care, timeUtil.canEditCare(care));
    }

    /*
    칭찬 처방 수정
    */
    @Transactional
    public void updateCare(User user, Long careId, CareRequest request) {
        Care care = findCareById(user, careId);
        canEdit(care);
        update(request, care);
    }

    /*
    칭찬 처방 조회
    */
    @Transactional(readOnly = true)
    public CareResponse getCare(User user, Long careId) {
        Care care = findCareById(user, careId);
        return CareResponse.of(care, timeUtil.canEditCare(care));
    }

    /*
    칭찬 처방 랜덤 조회
    */
    @Transactional(readOnly = true)
    public RandomCareResponse getRandomCare(User user) {
        long count = careRepository.countByUser(user);
        int randomIndex = (int) (Math.random() * count);

        Page<Care> carePage = getRandomOne(user, randomIndex); // 랜덤한 하나만 추출
        if (carePage.hasContent()) {
            return RandomCareResponse.of(carePage.getContent().get(0));
        } else {
            return null;
        }
    }

    /*
    칭찬 처방 히스토리 조회
    */
    @Transactional(readOnly = true)
    public Map<String, List<CareHistoryResponse>> getCareHistroy(User user, CareSearchFilter filter) {
        List<Care> careList = careRepository.searchCareHistory(user, filter);
        return getCareListDto(careList);
    }

    /*
    칭찬 처방 전체 삭제
    */
    @Transactional
    public void deleteAll(User user) {
        List<Care> careList = careRepository.findAllByUser(user);
        for (Care care : careList) {
            Mytamin mytamin = care.getMytamin();
            delete(care, mytamin);

            // 마이타민과 연관된 데이터가 하나도 없다면 -> 마이타민도 삭제
            if (mytamin.getReport() == null) mytaminService.deleteMytamin(user, mytamin.getMytaminId());
        }
    }

    private Care findCareById(User user, Long careId) {
        return careRepository.findByUserAndCareId(user, careId)
                .orElseThrow(() -> new MytaminException(CARE_NOT_FOUND_ERROR));
    }

    private void canEdit(Care care) {
        if (!timeUtil.isInRange(LocalDateTime.now(), care.getCreatedAt(), care.getCreatedAt().plusDays(1))) {
            throw new MytaminException(EDIT_TIMEOUT_ERROR);
        }
    }

    private Care save(User user, CareRequest careRequest, Mytamin mytamin) {
        Care care = new Care(
                user,
                validateCode(careRequest.getCareCategoryCode()),
                careRequest.getCareMsg1(),
                careRequest.getCareMsg2(),
                mytamin
        );
        Care newCare = careRepository.save(care);
        mytamin.updateCare(newCare);
        return newCare;
    }

    private void update(CareRequest request, Care care) {
        care.updateAll(
                validateCode(request.getCareCategoryCode()),
                request.getCareMsg1(),
                request.getCareMsg2()
        );
        careRepository.save(care);
    }

    private void delete(Care care, Mytamin mytamin) {
        mytamin.updateCare(null); // Mytamin과 연관관계 끊기
        careRepository.delete(care);
    }

    private Page<Care> getRandomOne(User user, int randomIndex) {
        return careRepository
                .findAllByUser(
                        user,
                        PageRequest.of(randomIndex, 1)
                );
    }

    private Map<String, List<CareHistoryResponse>> getCareListDto(List<Care> careList) {
        Map<String, List<CareHistoryResponse>> map = new LinkedHashMap<>();
        for (Care care : careList) {
            CareHistoryResponse careResponse = CareHistoryResponse.of(care);
            List<CareHistoryResponse> list = map.getOrDefault(careResponse.getTitle(), new ArrayList<>());
            list.add(careResponse);
            map.put(careResponse.getTitle(), list);
        }
        return map;
    }

}
