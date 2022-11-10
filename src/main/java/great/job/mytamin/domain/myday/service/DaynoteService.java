package great.job.mytamin.domain.myday.service;

import great.job.mytamin.domain.myday.dto.request.DaynoteRequest;
import great.job.mytamin.domain.myday.dto.request.DaynoteUpdateRequest;
import great.job.mytamin.domain.myday.dto.response.DaynoteResponse;
import great.job.mytamin.domain.myday.entity.Daynote;
import great.job.mytamin.domain.myday.entity.Wish;
import great.job.mytamin.domain.myday.repository.DaynoteRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.util.TimeUtil;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import static great.job.mytamin.global.exception.ErrorMap.DAYNOTE_ALREADY_EXIST_ERROR;
import static great.job.mytamin.global.exception.ErrorMap.DAYNOTE_NOT_FOUND_ERROR;

@Service
@RequiredArgsConstructor
public class DaynoteService {

    private final TimeUtil timeUtil;
    private final WishService wishService;
    private final AwsS3Service awsS3Service;
    private final DaynoteRepository daynoteRepository;

    /*
    데이노트 작성 가능 여부
    */
    @Transactional(readOnly = true)
    public Boolean canCreateDaynote(User user, String date) {
        LocalDateTime performedAt = timeUtil.convertRawToLocalDateTime(date);
        return daynoteRepository.findByUserAndPerformedAt(user, performedAt).isEmpty();
    }

    /*
    데이노트 작성하기
    */
    @Transactional
    public DaynoteResponse createDaynote(User user, DaynoteRequest request) {
        Wish wish = wishService.findWishById(user, Long.parseLong(request.getWishId()));
        if (!canCreateDaynote(user, request.getDate()))
            throw new MytaminException(DAYNOTE_ALREADY_EXIST_ERROR);
        return DaynoteResponse.of(saveNewDaynote(request, wish, user));
    }

    /*
    데이노트 수정
    */
    @Transactional
    public void updateDaynote(User user, Long daynoteId, DaynoteUpdateRequest request) {
        Daynote daynote = findDaynoteById(user, daynoteId);
        awsS3Service.deleteImgList(daynote.getImgUrlList()); // 기존 이미지 삭제

        Wish wish = wishService.findWishById(user, Long.parseLong(request.getWishId()));
        daynote.updateAll(
                awsS3Service.uploadImageList(request.getFileList(), "DN"),
                wish,
                request.getNote()
        );
        daynoteRepository.save(daynote);
    }

    /*
    데이노트 삭제
    */
    @Transactional
    public void deleteDaynote(User user, Long daynoteId) {
        Daynote daynote = findDaynoteById(user, daynoteId);
        awsS3Service.deleteImgList(daynote.getImgUrlList()); // 기존 이미지 삭제
        daynoteRepository.delete(daynote);
    }

    /*
    데이노트 리스트 조회
    */
    @Transactional(readOnly = true)
    public Map<Integer, List<DaynoteResponse>> getDaynoteList(User user) {
        List<Daynote> daynoteList = daynoteRepository.searchDaynoteList(user);

        Map<Integer, List<DaynoteResponse>> map = new LinkedHashMap<>();
        for (Daynote daynote : daynoteList) {
            DaynoteResponse daynoteRes = DaynoteResponse.of(daynote); // DTO 변환
            List<DaynoteResponse> list = map.getOrDefault(daynoteRes.getYear(), new ArrayList<>());
            list.add(daynoteRes);
            map.put(daynoteRes.getYear(), list);
        }

        return map;
    }

    /*
    데이노트 전체 삭제
    */
    @Transactional
    public void deleteAll(User user) {
        List<Daynote> daynoteList = daynoteRepository.findAllByUser(user);
        for (Daynote daynote : daynoteList) {
            awsS3Service.deleteImgList(daynote.getImgUrlList()); // 기존 이미지 삭제
            daynoteRepository.delete(daynote);
        }
    }

    private Daynote saveNewDaynote(DaynoteRequest daynoteRequest, Wish wish, User user) {
        Daynote daynote = new Daynote(
                awsS3Service.uploadImageList(daynoteRequest.getFileList(), "DN"),
                wish,
                daynoteRequest.getNote(),
                timeUtil.convertRawToLocalDateTime(daynoteRequest.getDate()),
                user
        );
        return daynoteRepository.save(daynote);
    }

    private Daynote findDaynoteById(User user, Long daynoteId) {
        return daynoteRepository.findByUserAndDaynoteId(user, daynoteId)
                .orElseThrow(() -> new MytaminException(DAYNOTE_NOT_FOUND_ERROR));
    }

}
