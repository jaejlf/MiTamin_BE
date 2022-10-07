package great.job.mytamin.domain.user.service;

import great.job.mytamin.domain.user.dto.response.MydayResponse;
import great.job.mytamin.domain.user.dto.response.ProfileResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.util.MydayUtil;
import great.job.mytamin.global.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MypageService {

    private final TimeUtil timeUtil;
    private final MydayUtil mydayUtil;

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
        if (!timeUtil.isCurrentMonth(dateOfMyday)) dateOfMyday = updateDateOfMyday(user);

        Map<String, String> map = timeUtil.getMyDayInfo(user.getNickname(), dateOfMyday);
        return MydayResponse.of(
                dateOfMyday.format(DateTimeFormatter.ofPattern("MM월 dd일")),
                map.get("dday"),
                map.get("msg"));
    }

    private LocalDateTime updateDateOfMyday(User user) {
        LocalDateTime dateOfMyday = mydayUtil.randomizeDateOfMyday();
        user.updateDateOfMyday(dateOfMyday);
        return dateOfMyday;
    }

}
