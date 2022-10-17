package great.job.mytamin.domain.user.service;

import great.job.mytamin.domain.user.dto.request.MytaminAlarmRequest;
import great.job.mytamin.domain.user.dto.response.SettingInfoResponse;
import great.job.mytamin.domain.user.dto.response.SettingResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.enumerate.MydayAlarm;
import great.job.mytamin.domain.user.repository.UserRepository;
import great.job.mytamin.domain.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SettingService {

    private final TimeUtil timeUtil;
    private final UserRepository userRepository;

    /*
    알림 설정 상태 조회
    */
    @Transactional(readOnly = true)
    public SettingResponse getAlarmSetting(User user) {
        // 마이타민
        String mytaminWhen = null;
        Boolean mytaminAlarmOn = user.getMytaminAlarmOn();
        if (mytaminAlarmOn) mytaminWhen = timeUtil.convertToAlarmFormat(user.getMytaminHour(), user.getMytaminMin());
        SettingInfoResponse mytamin = SettingInfoResponse.of(mytaminAlarmOn, mytaminWhen);

        // 마이데이
        String mydayWhen = null;
        Boolean mydayAlarmOn = user.getMydayAlarmOn();
        if (mydayAlarmOn) mydayWhen = user.getMydayWhen();
        SettingInfoResponse myday = SettingInfoResponse.of(mydayAlarmOn, mydayWhen);

        return SettingResponse.of(mytamin, myday);
    }

    /*
    마이타민 알림 ON
    */
    @Transactional
    public void mytaminAlarmOn(User user, MytaminAlarmRequest mytaminAlarmRequest) {
        user.updateMytaminAlarmOn(true);
        user.updateMytaminWhen(
                mytaminAlarmRequest.getMytaminHour(),
                mytaminAlarmRequest.getMytaminMin()
        );
        userRepository.save(user);
    }

    /*
    마이타민 알림 OFF
    */
    public void mytaminAlarmOff(User user) {
        user.updateMytaminAlarmOn(false);
        userRepository.save(user);
    }

    /*
    마이데이 알림 ON
    */
    @Transactional
    public void mydayAlarmOn(User user, int code) {
        user.updateMydayAlarmOn(true);
        user.updateMydayWhen(MydayAlarm.convertCodeToMsg(code));
        userRepository.save(user);
    }

    /*
    마이데이 알림 OFF
    */
    public void mydayAlarmOff(User user) {
        user.updateMydayAlarmOn(false);
        userRepository.save(user);
    }

    // 마이타민 섭취 지정 시간 -> valid check

}
