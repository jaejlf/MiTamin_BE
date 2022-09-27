package great.job.mytamin.domain.care.service;

import great.job.mytamin.domain.care.dto.request.CareRequest;
import great.job.mytamin.domain.care.dto.response.CareResponse;
import great.job.mytamin.domain.care.entity.Care;
import great.job.mytamin.domain.care.enumerate.CareCategory;
import great.job.mytamin.domain.care.repository.CareRepository;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.service.MytaminService;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.exception.MytaminException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static great.job.mytamin.global.exception.ErrorMap.CARE_ALREADY_DONE;

@Service
@RequiredArgsConstructor
public class CareService {

    private final MytaminService mytaminService;
    private final CareRepository careRepository;

    /*
    칭찬 처방하기
    */
    @Transactional
    public CareResponse careToday(User user, CareRequest careRequest) {
        Mytamin mytamin = mytaminService.getMytamin(user);
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

}
