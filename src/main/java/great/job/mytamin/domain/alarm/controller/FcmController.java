package great.job.mytamin.domain.alarm.controller;

import com.google.firebase.messaging.FirebaseMessagingException;
import great.job.mytamin.domain.alarm.service.FcmService;
import great.job.mytamin.domain.alarm.dto.request.FcmRequest;
import great.job.mytamin.global.dto.response.NoDataResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/fcm")
public class FcmController {

    private final FcmService fcmService;

    @PostMapping("/test")
    public ResponseEntity<Object> pushMessage(@RequestBody FcmRequest request) throws FirebaseMessagingException {
        fcmService.sendTargetMessage(request.getTargetToken(), request.getTitle(), request.getBody());
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("푸시 알림 테스트"));
    }

}
