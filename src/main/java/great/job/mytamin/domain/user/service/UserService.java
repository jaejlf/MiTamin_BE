package great.job.mytamin.domain.user.service;

import great.job.mytamin.domain.user.dto.response.MydayResponse;
import great.job.mytamin.domain.user.dto.response.ProfileResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.util.UserUtil;
import great.job.mytamin.global.service.TimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TimeService timeService;
    private final UserUtil userUtil;

    /*
    마이페이지 프로필 조회
    */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(User user) {
        return ProfileResponse.of(user);
    }

    /*
    이번 달의 마이데이
    */
    @Transactional(readOnly = true)
    public MydayResponse getMyday(User user) {
        LocalDateTime dateOfMyday = user.getDateOfMyday();

        // 마이데이 날짜 업데이트
        if (!timeService.isCurrentMonth(dateOfMyday)) {
            dateOfMyday = randomizeDateOfMyday();
            user.updateDateOfMyday(randomizeDateOfMyday());
        }

        Map<String, String> map = timeService.getMyDayInfo(user.getNickname(), dateOfMyday);
        return MydayResponse.of(
                dateOfMyday.format(DateTimeFormatter.ofPattern("MM월 dd일")),
                map.get("dDay"),
                map.get("msg"));
    }

    /*
    마이데이 날짜 랜덤 지정
    */
    @Transactional(readOnly = true)
    public LocalDateTime randomizeDateOfMyday() {
        LocalDateTime now = LocalDateTime.now();

        // 15일 ~ 30일
        int rangeStart = 15;
        int rangeEnd = 30;
        int randomizeDay = (int) (Math.random() * (rangeEnd - rangeStart + 1)) + rangeStart;

        // 랜덤 날짜 리턴
        return LocalDateTime.of(
                now.getYear(),
                now.getMonth().getValue(),
                randomizeDay,
                0, 0);
    }

    /*
    닉네임 수정
    */
    @Transactional
    public void editNickname(User user, String nickname) {
        userUtil.checkNicknameDuplication(nickname);
        user.updateNickname(nickname);
    }

    /*
    '되고 싶은 나' 메세지 수정
    */
    @Transactional
    public void editBeMyMessage(User user, String msg) {
        user.updateBeMyMessage(msg);
    }
    
}
