package great.job.mytamin.domain.user.service;

import great.job.mytamin.domain.user.dto.request.MytaminAlarmRequest;
import great.job.mytamin.domain.user.dto.response.SettingInfoResponse;
import great.job.mytamin.domain.user.dto.response.SettingResponse;
import great.job.mytamin.domain.user.entity.FcmOn;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.repository.FcmOnRepository;
import great.job.mytamin.domain.user.repository.UserRepository;
import great.job.mytamin.domain.util.TimeUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static great.job.mytamin.domain.myday.enumerate.MydayAlarm.NONE;
import static great.job.mytamin.domain.myday.enumerate.MydayAlarm.convertCodeToMsg;

@Service
@RequiredArgsConstructor
public class AlarmService {

    private final TimeUtil timeUtil;
    private final UserRepository userRepository;
    private final FcmOnRepository fcmOnRepository;

    /*
    알림 설정 상태 조회
    */
    @Transactional(readOnly = true)
    public SettingResponse getAlarmSettingStatus(User user) {
        SettingInfoResponse mytamin = getMytaminSettingStatus(user);
        SettingInfoResponse myday = getMydaySettingStatus(user);
        return SettingResponse.of(mytamin, myday);
    }

    /*
    마이타민 알림 ON
    */
    @Transactional
    public void turnOnMytaminAlarm(User user, MytaminAlarmRequest request) {
        updateMytaminWhen(user, request);
        updateFcmOn(user, request);
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
        user.getAlarm().updateMydayWhen(NONE.getMsg());
        userRepository.save(user);
    }

    private SettingInfoResponse getMytaminSettingStatus(User user) {
        String mytaminWhen = null;
        Boolean mytaminAlarmOn = user.getAlarm().getMytaminAlarmOn();

        if (mytaminAlarmOn) mytaminWhen = timeUtil.convertToAlarmFormat( // 알림이 켜져있는 경우 시간 정보 설정
                user.getAlarm().getMytaminHour(),
                user.getAlarm().getMytaminMin());
        return SettingInfoResponse.of(mytaminAlarmOn, mytaminWhen);
    }

    private SettingInfoResponse getMydaySettingStatus(User user) {
        String mydayWhen = null;
        Boolean mydayAlarmOn = user.getAlarm().getMydayAlarmOn();

        if (mydayAlarmOn) mydayWhen = user.getAlarm().getMydayWhen(); // 알림이 켜져있는 경우 시간 정보 설정
        return SettingInfoResponse.of(mydayAlarmOn, mydayWhen);
    }

    private void updateMytaminWhen(User user, MytaminAlarmRequest request) {
        timeUtil.isTimeValid(request.getMytaminHour(), request.getMytaminMin());
        user.getAlarm().updateMytaminAlarmOn(true);
        user.getAlarm().updateMytaminWhen(
                request.getMytaminHour(),
                request.getMytaminMin()
        );
        userRepository.save(user);
    }

    private void updateFcmOn(User user, MytaminAlarmRequest request) {
        String fcmToken = request.getFcmToken();
        FcmOn fcmOn = fcmOnRepository.findByFcmToken(fcmToken);
        if (fcmOn == null) {
            fcmOnRepository.save(new FcmOn(user, fcmToken));
        } else {
            fcmOn.updateMytaminWhen(user);
        }
    }

}