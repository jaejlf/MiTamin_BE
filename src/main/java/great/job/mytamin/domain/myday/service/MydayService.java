package great.job.mytamin.domain.myday.service;

import great.job.mytamin.domain.myday.dto.response.MydayResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.util.MydayUtil;
import great.job.mytamin.domain.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class MydayService {

    private final TimeUtil timeUtil;
    private final MydayUtil mydayUtil;

    /*
    이번 달의 마이데이
    */
    @Transactional(readOnly = true)
    public MydayResponse getMyday(User user) {
        LocalDateTime dateOfMyday = user.getAlarm().getDateOfMyday();
        if (!timeUtil.isCurrentMonth(dateOfMyday)) {
            dateOfMyday = updateDateOfMyday(user); // 저장된 데이터가 이번 달의 날짜가 아니라면 업데이트
        }
        return getMydayDto(user, dateOfMyday);
    }

    private LocalDateTime updateDateOfMyday(User user) {
        LocalDateTime dateOfMyday = mydayUtil.randomizeDateOfMyday();
        user.getAlarm().updateDateOfMyday(dateOfMyday);
        return dateOfMyday;
    }

    private MydayResponse getMydayDto(User user, LocalDateTime dateOfMyday) {
        Map<String, String> map = timeUtil.getMydayInfo(user.getNickname(), dateOfMyday);
        return MydayResponse.of(
                dateOfMyday.format(DateTimeFormatter.ofPattern("MM월 dd일")),
                map.get("dday"),
                map.get("comment"));
    }

}
