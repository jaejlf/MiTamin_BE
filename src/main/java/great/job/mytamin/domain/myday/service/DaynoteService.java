package great.job.mytamin.domain.myday.service;

import great.job.mytamin.domain.myday.dto.request.DaynoteRequest;
import great.job.mytamin.domain.myday.dto.request.DaynoteUpdateRequest;
import great.job.mytamin.domain.myday.dto.response.DaynoteListResponse;
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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static great.job.mytamin.global.exception.ErrorMap.*;

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
    public Boolean canCreateDaynote(User user, String performedAt) {
        LocalDateTime rawPerformedAt = timeUtil.convertToRawPerformedAt(performedAt);
        return daynoteRepository.findByUserAndRawPerformedAt(user, rawPerformedAt).isEmpty();
    }

    /*
    데이노트 작성하기
    */
    @Transactional
    public DaynoteResponse createDaynote(User user, DaynoteRequest daynoteRequest) {
        System.out.println("1:" + daynoteRequest.getNote());
        System.out.println("2:" + daynoteRequest.getWishText());
        System.out.println("3:" + daynoteRequest.getPerformedAt());
        Wish wish = wishService.findWishOrElseNew(user, daynoteRequest.getWishText());
        if (!canCreateDaynote(user, daynoteRequest.getPerformedAt()))
            throw new MytaminException(DAYNOTE_ALREADY_EXIST_ERROR);
        return DaynoteResponse.ofDetail(saveNewDaynote(daynoteRequest, wish, user));
    }

    /*
    데이노트 조회
    */
    public DaynoteResponse getDaynote(Long daynoteId) {
        Daynote daynote = findDaynoteById(daynoteId);
        return DaynoteResponse.ofDetail(daynote);
    }

    /*
    데이노트 수정
    */
    @Transactional
    public void updateDaynote(User user, Long daynoteId, DaynoteUpdateRequest daynoteUpdateRequest) {
        Daynote daynote = findDaynoteById(daynoteId);
        hasAuthorized(daynote, user);
        awsS3Service.deleteImgList(daynote.getImgUrlList()); //기존 이미지 삭제

        Wish wish = wishService.findWishOrElseNew(user, daynoteUpdateRequest.getWishText());
        daynote.updateAll(
                awsS3Service.uploadImageList(daynoteUpdateRequest.getFileList(), "DN"),
                wish,
                daynoteUpdateRequest.getNote()
        );
        daynoteRepository.save(daynote);
    }

    /*
    데이노트 삭제
    */
    @Transactional
    public void deleteDaynote(User user, Long daynoteId) {
        Daynote daynote = findDaynoteById(daynoteId);
        hasAuthorized(daynote, user);
        awsS3Service.deleteImgList(daynote.getImgUrlList()); //기존 이미지 삭제
        daynoteRepository.delete(daynote);
    }

    /*
    데이노트 리스트 조회
    */
    @Transactional(readOnly = true)
    public DaynoteListResponse getDaynoteList(User user) {
        List<Daynote> daynoteList = daynoteRepository.findByUser(user);
        daynoteList.sort(Comparator.comparing(Daynote::getRawPerformedAt)); // 날짜 오름차순 정렬

        Map<Integer, List<DaynoteResponse>> daynoteListMap =
                daynoteList.stream().map(DaynoteResponse::ofOverview).collect(Collectors.toList()) // DTO 변환
                        .stream().collect(Collectors.groupingBy(DaynoteResponse::getYear)); // year로 그룹핑

        return DaynoteListResponse.of(daynoteListMap);
    }

    private Daynote saveNewDaynote(DaynoteRequest daynoteRequest, Wish wish, User user) {
        Daynote daynote = new Daynote(
                awsS3Service.uploadImageList(daynoteRequest.getFileList(), "DN"),
                wish,
                daynoteRequest.getNote(),
                timeUtil.convertToRawPerformedAt(daynoteRequest.getPerformedAt()),
                user
        );
        return daynoteRepository.save(daynote);
    }

    private Daynote findDaynoteById(Long daynoteId) {
        return daynoteRepository.findById(daynoteId)
                .orElseThrow(() -> new MytaminException(DAYNOTE_NOT_FOUND_ERROR));
    }

    private void hasAuthorized(Daynote daynote, User user) {
        if (!daynote.getUser().equals(user)) {
            throw new MytaminException(NO_AUTH_ERROR);
        }
    }

}
