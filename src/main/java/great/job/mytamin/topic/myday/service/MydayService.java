package great.job.mytamin.topic.myday.service;

import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.topic.myday.dto.request.DaynoteRequest;
import great.job.mytamin.topic.myday.dto.response.DaynoteResponse;
import great.job.mytamin.topic.myday.dto.response.MydayResponse;
import great.job.mytamin.topic.myday.entity.Daynote;
import great.job.mytamin.topic.myday.entity.Wish;
import great.job.mytamin.topic.myday.repository.DaynoteRepository;
import great.job.mytamin.topic.user.entity.User;
import great.job.mytamin.topic.util.MydayUtil;
import great.job.mytamin.topic.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static great.job.mytamin.global.exception.ErrorMap.DAYNOTE_ALREADY_DONE_ERROR;

@Service
@RequiredArgsConstructor
public class MydayService {

    private final TimeUtil timeUtil;
    private final MydayUtil mydayUtil;
    private final WishService wishService;
    private final DaynoteRepository daynoteRepository;

    /*
    이번 달의 마이데이
    */
    @Transactional(readOnly = true)
    public MydayResponse getMyday(User user) {
        LocalDateTime dateOfMyday = user.getDateOfMyday();
        if (!timeUtil.isCurrentMonth(dateOfMyday)) dateOfMyday = updateDateOfMyday(user);

        Map<String, String> map = timeUtil.getMyDayInfo(user.getNickname(), dateOfMyday);
        return MydayResponse.of(
                dateOfMyday.format(DateTimeFormatter.ofPattern("MM월 dd일")),
                map.get("dday"),
                map.get("msg"));
    }

    /*
    데이노트 작성하기
    */
    @Transactional
    public DaynoteResponse createDaynote(User user, DaynoteRequest daynoteRequest) {
        Wish wish = wishService.findWishOrElseNew(user, daynoteRequest.getWishText());
        LocalDateTime rawPerformedAt = LocalDateTime.of(daynoteRequest.getYear(), daynoteRequest.getMonth(), 10, 10, 0); // 일, 시, 분은 더미 데이터 set

        checkExistence(user, rawPerformedAt);
        return DaynoteResponse.of(saveNewDaynote(daynoteRequest, wish, rawPerformedAt, user));
    }

    private LocalDateTime updateDateOfMyday(User user) {
        LocalDateTime dateOfMyday = mydayUtil.randomizeDateOfMyday();
        user.updateDateOfMyday(dateOfMyday);
        return dateOfMyday;
    }

    private void checkExistence(User user, LocalDateTime rawPerformedAt) {
        if (daynoteRepository.findByUserAndRawPerformedAt(user, rawPerformedAt).isPresent()) {
            throw new MytaminException(DAYNOTE_ALREADY_DONE_ERROR);
        }
    }

    private Daynote saveNewDaynote(DaynoteRequest daynoteRequest, Wish wish, LocalDateTime rawPerformedAt, User user) {
        Daynote daynote = new Daynote(
                null, // 이미지 리스트 업로드 구현 전
                wish,
                daynoteRequest.getNote(),
                rawPerformedAt,
                user
        );
        return daynoteRepository.save(daynote);
    }

}
