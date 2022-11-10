package great.job.mytamin.domain.user.service;

import great.job.mytamin.domain.user.dto.request.MytaminAlarmRequest;
import great.job.mytamin.domain.user.dto.response.SettingInfoResponse;
import great.job.mytamin.domain.user.dto.response.SettingResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.repository.UserRepository;
import great.job.mytamin.domain.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static great.job.mytamin.domain.user.enumerate.MydayAlarm.convertCodeToMsg;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final TimeUtil timeUtil;
    private final UserRepository userRepository;

    /*
    알림 설정 상태 조회
    */
    @Transactional(readOnly = true)
    public SettingResponse getAlarmSettingStatus(User user) {
        // 마이타민
        String mytaminWhen = null;
        Boolean mytaminAlarmOn = user.getAlarm().getMytaminAlarmOn();
        if (mytaminAlarmOn) mytaminWhen = timeUtil.convertToAlarmFormat(user.getAlarm().getMytaminHour(), user.getAlarm().getMytaminMin());
        SettingInfoResponse mytamin = SettingInfoResponse.of(mytaminAlarmOn, mytaminWhen);

        // 마이데이
        String mydayWhen = null;
        Boolean mydayAlarmOn = user.getAlarm().getMydayAlarmOn();
        if (mydayAlarmOn) mydayWhen = user.getAlarm().getMydayWhen();
        SettingInfoResponse myday = SettingInfoResponse.of(mydayAlarmOn, mydayWhen);

        return SettingResponse.of(mytamin, myday);
    }

    /*
    마이타민 알림 ON
    */
    @Transactional
    public void turnOnMytaminAlarm(User user, MytaminAlarmRequest request) {
        timeUtil.isTimeValid(request.getMytaminHour(), request.getMytaminMin());
        user.getAlarm().updateMytaminAlarmOn(true);
        user.getAlarm().updateMytaminWhen(
                request.getMytaminHour(),
                request.getMytaminMin()
        );
        userRepository.save(user);
    }

    /*
    마이타민 알림 OFF
    */
    @Transactional
    public void turnOffMytaminAlarm(User user) {
        user.getAlarm().updateMytaminAlarmOn(false);
        userRepository.save(user);
    }

    /*
    마이데이 알림 ON
    */
    @Transactional
    public void turnOnMydayAlarm(User user, int code) {
        user.getAlarm().updateMydayAlarmOn(true);
        user.getAlarm().updateMydayWhen(convertCodeToMsg(code));
        userRepository.save(user);
    }

    /*
    마이데이 알림 OFF
    */
    @Transactional
    public void turnOffMydayAlarm(User user) {
        user.getAlarm().updateMydayAlarmOn(false);
        userRepository.save(user);
    }

}