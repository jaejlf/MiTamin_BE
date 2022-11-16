package great.job.mytamin.domain.alarm.controller;

import great.job.mytamin.domain.user.dto.request.MytaminAlarmRequest;
import great.job.mytamin.domain.user.dto.response.SettingResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.alarm.service.AlarmService;
import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/alarm")
public class AlarmController {

    private final AlarmService alarmService;

    @GetMapping("/status")
    public ResponseEntity<Object> getAlarmSettingStatus(@AuthenticationPrincipal User user) {
        SettingResponse result = alarmService.getAlarmSettingStatus(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("알림 설정 상태 조회", result));
    }

    @PatchMapping("/mytamin/on")
    public ResponseEntity<Object> turnOnMytaminAlarm(@AuthenticationPrincipal User user,
                                                     @RequestBody MytaminAlarmRequest request) {
        alarmService.turnOnMytaminAlarm(user, request);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("마이타민 알림 ON"));
    }

    @PatchMapping("/mytamin/off")
    public ResponseEntity<Object> turnOffMytaminAlarm(@AuthenticationPrincipal User user) {
        alarmService.turnOffMytaminAlarm(user);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("마이타민 알림 OFF"));
    }

    @PatchMapping("/myday/on/{code}")
    public ResponseEntity<Object> turnOnmydayAlarm(@AuthenticationPrincipal User user,
                                                   @PathVariable int code) {
        alarmService.turnOnMydayAlarm(user, code);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("마이데이 알림 ON"));
    }

    @PatchMapping("/myday/off")
    public ResponseEntity<Object> turnOffMydayAlarm(@AuthenticationPrincipal User user) {
        alarmService.turnOffMydayAlarm(user);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("마이데이 알림 OFF"));
    }

}
