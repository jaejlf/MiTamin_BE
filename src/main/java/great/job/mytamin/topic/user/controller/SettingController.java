package great.job.mytamin.topic.user.controller;

import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import great.job.mytamin.topic.user.dto.request.MytaminAlarmRequest;
import great.job.mytamin.topic.user.dto.response.SettingResponse;
import great.job.mytamin.topic.user.entity.User;
import great.job.mytamin.topic.user.service.SettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/setting")
public class SettingController {

    private final SettingService settingService;

    @GetMapping("/alarm")
    public ResponseEntity<Object> getAlarmSetting(@AuthenticationPrincipal User user) {
        SettingResponse settingResponse = settingService.getAlarmSetting(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("알림 설정 상태 조회", settingResponse));
    }

    @PatchMapping("/alarm/mytamin/on")
    public ResponseEntity<Object> mytaminAlarmOn(@AuthenticationPrincipal User user,
                                                 @RequestBody MytaminAlarmRequest mytaminAlarmRequest) {
        settingService.mytaminAlarmOn(user, mytaminAlarmRequest);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("마이타민 알림 ON"));
    }

    @PatchMapping("/alarm/mytamin/off")
    public ResponseEntity<Object> mytaminAlarmOff(@AuthenticationPrincipal User user) {
        settingService.mytaminAlarmOff(user);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("마이타민 알림 OFF"));
    }

    @PatchMapping("/alarm/myday/on/{code}")
    public ResponseEntity<Object> mytaminAlarmOn(@AuthenticationPrincipal User user,
                                                 @PathVariable int code) {
        settingService.mydayAlarmOn(user, code);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("마이데이 알림 ON"));
    }

    @PatchMapping("/alarm/myday/off")
    public ResponseEntity<Object> mytaminAlarmOn(@AuthenticationPrincipal User user) {
        settingService.mydayAlarmOff(user);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("마이데이 알림 OFF"));
    }

}
