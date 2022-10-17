package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.dto.request.MytaminAlarmRequest;
import great.job.mytamin.domain.user.dto.response.SettingResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.service.SettingService;
import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
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
    public ResponseEntity<Object> getAlarmSettingStatus(@AuthenticationPrincipal User user) {
        SettingResponse settingResponse = settingService.getAlarmSettingStatus(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("알림 설정 상태 조회", settingResponse));
    }

    @PatchMapping("/alarm/mytamin/on")
    public ResponseEntity<Object> turnOnMytaminAlarm(@AuthenticationPrincipal User user,
                                                     @RequestBody MytaminAlarmRequest mytaminAlarmRequest) {
        settingService.turnOnMytaminAlarm(user, mytaminAlarmRequest);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("마이타민 알림 ON"));
    }

    @PatchMapping("/alarm/mytamin/off")
    public ResponseEntity<Object> turnOffMytaminAlarm(@AuthenticationPrincipal User user) {
        settingService.turnOffMytaminAlarm(user);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("마이타민 알림 OFF"));
    }

    @PatchMapping("/alarm/myday/on/{code}")
    public ResponseEntity<Object> turnOnmydayAlarm(@AuthenticationPrincipal User user,
                                                   @PathVariable int code) {
        settingService.turnOnMydayAlarm(user, code);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("마이데이 알림 ON"));
    }

    @PatchMapping("/alarm/myday/off")
    public ResponseEntity<Object> turnOffMydayAlarm(@AuthenticationPrincipal User user) {
        settingService.turnOffMydayAlarm(user);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("마이데이 알림 OFF"));
    }

}
