package great.job.mytamin.topic.myday.controller;

import great.job.mytamin.global.dto.response.ResultResponse;
import great.job.mytamin.topic.myday.dto.request.DaynoteRequest;
import great.job.mytamin.topic.myday.dto.response.DaynoteResponse;
import great.job.mytamin.topic.myday.dto.response.MydayResponse;
import great.job.mytamin.topic.myday.service.MydayService;
import great.job.mytamin.topic.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/myday")
public class MydayController {

    private final MydayService mydayService;

    @GetMapping("/info")
    public ResponseEntity<Object> getMyday(@AuthenticationPrincipal User user) {
        MydayResponse mydayResponse = mydayService.getMyday(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("이번 달의 마이데이", mydayResponse));
    }
    
    @PostMapping("/note")
    public ResponseEntity<Object> createDaynote(@AuthenticationPrincipal User user,
                                                @RequestBody DaynoteRequest daynoteRequest) {
        DaynoteResponse daynoteResponse = mydayService.createDaynote(user, daynoteRequest);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("데이노트 작성하기", daynoteResponse));
    }
    
}
