package great.job.mytamin.topic.myday.service;

import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.service.AwsS3Service;
import great.job.mytamin.topic.myday.dto.request.DaynoteRequest;
import great.job.mytamin.topic.myday.dto.response.DaynoteListResponse;
import great.job.mytamin.topic.myday.dto.response.DaynoteResponse;
import great.job.mytamin.topic.myday.entity.Daynote;
import great.job.mytamin.topic.myday.entity.Wish;
import great.job.mytamin.topic.myday.repository.DaynoteRepository;
import great.job.mytamin.topic.user.entity.User;
import great.job.mytamin.topic.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static great.job.mytamin.global.exception.ErrorMap.DAYNOTE_ALREADY_EXIST_ERROR;

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
    public Boolean canCreateDaynote(User user, String performedAt) {
        LocalDateTime rawPerformedAt = timeUtil.convertToRawPerformedAt(performedAt);
        return daynoteRepository.findByUserAndRawPerformedAt(user, rawPerformedAt).isEmpty();
    }

    /*
    데이노트 작성하기
    */
    @Transactional
    public DaynoteResponse createDaynote(User user, List<MultipartFile> fileList, DaynoteRequest daynoteRequest) {
        Wish wish = wishService.findWishOrElseNew(user, daynoteRequest.getWishText());
        if (!canCreateDaynote(user, daynoteRequest.getPerformedAt()))
            throw new MytaminException(DAYNOTE_ALREADY_EXIST_ERROR);
        return DaynoteResponse.ofDetail(saveNewDaynote(fileList, daynoteRequest, wish, user));
    }

    private Daynote saveNewDaynote(List<MultipartFile> fileList, DaynoteRequest daynoteRequest, Wish wish, User user) {
        Daynote daynote = new Daynote(
                awsS3Service.uploadImageList(fileList, "DN"),
                wish,
                daynoteRequest.getNote(),
                timeUtil.convertToRawPerformedAt(daynoteRequest.getPerformedAt()),
                user
        );
        return daynoteRepository.save(daynote);
    }

    /*
    데이노트 리스트 조회
    */
    public DaynoteListResponse getDaynoteList(User user) {
        List<Daynote> daynoteList = daynoteRepository.findByUser(user);
        daynoteList.sort(Comparator.comparing(Daynote::getRawPerformedAt)); // 날짜 오름차순 정렬

        Map<Integer, List<DaynoteResponse>> daynoteListMap =
                daynoteList.stream().map(DaynoteResponse::ofOverview).collect(Collectors.toList()) // DTO 변환
                        .stream().collect(Collectors.groupingBy(DaynoteResponse::getYear)); // year로 그룹핑

        return DaynoteListResponse.of(daynoteListMap);
    }

}
