package great.job.mytamin.topic.myday.controller;

import great.job.mytamin.global.dto.response.ResultResponse;
import great.job.mytamin.topic.myday.dto.request.DaynoteRequest;
import great.job.mytamin.topic.myday.dto.response.DaynoteListResponse;
import great.job.mytamin.topic.myday.dto.response.DaynoteResponse;
import great.job.mytamin.topic.myday.service.DaynoteService;
import great.job.mytamin.topic.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daynote")
public class DaynoteController {

    private final DaynoteService daynoteService;

    @GetMapping("/check/{performedAt}")
    public ResponseEntity<Object> canCreateDaynote(@AuthenticationPrincipal User user,
                                                   @PathVariable String performedAt) {
        Boolean canCreate = daynoteService.canCreateDaynote(user, performedAt);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("데이노트 작성 가능 여부", canCreate));
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getDaynoteList(@AuthenticationPrincipal User user) {
        DaynoteListResponse daynoteListResponse = daynoteService.getDaynoteList(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("데이노트 리스트 조회", daynoteListResponse));
    }

    @PostMapping("/new")
    public ResponseEntity<Object> createDaynote(@AuthenticationPrincipal User user,
                                                @RequestBody DaynoteRequest daynoteRequest) {
        DaynoteResponse daynoteResponse = daynoteService.createDaynote(user, daynoteRequest);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("데이노트 작성하기", daynoteResponse));
    }

}
