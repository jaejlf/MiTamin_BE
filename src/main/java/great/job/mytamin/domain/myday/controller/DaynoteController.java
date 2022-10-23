package great.job.mytamin.domain.myday.controller;

import great.job.mytamin.domain.myday.dto.request.DaynoteRequest;
import great.job.mytamin.domain.myday.dto.request.DaynoteUpdateRequest;
import great.job.mytamin.domain.myday.dto.response.DaynoteResponse;
import great.job.mytamin.domain.myday.service.DaynoteService;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/daynote")
public class DaynoteController {

    private final DaynoteService daynoteService;

    @GetMapping("/check/{date}")
    public ResponseEntity<Object> canCreateDaynote(@AuthenticationPrincipal User user,
                                                   @PathVariable String date) {
        Boolean canCreate = daynoteService.canCreateDaynote(user, date);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("데이노트 작성 가능 여부", canCreate));
    }

    @PostMapping("/new")
    public ResponseEntity<Object> createDaynote(@AuthenticationPrincipal User user,
                                                @ModelAttribute DaynoteRequest daynoteRequest) {
        DaynoteResponse daynoteResponse = daynoteService.createDaynote(user, daynoteRequest);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("데이노트 작성하기", daynoteResponse));
    }

    @PutMapping("/{daynoteId}")
    public ResponseEntity<Object> updateDaynote(@AuthenticationPrincipal User user,
                                                @PathVariable Long daynoteId,
                                                @ModelAttribute DaynoteUpdateRequest daynoteUpdateRequest) {
        daynoteService.updateDaynote(user, daynoteId, daynoteUpdateRequest);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("데이노트 수정 완료"));
    }

    @DeleteMapping("/{daynoteId}")
    public ResponseEntity<Object> deleteDaynote(@AuthenticationPrincipal User user,
                                                @PathVariable Long daynoteId) {
        daynoteService.deleteDaynote(user, daynoteId);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("데이노트 삭제 완료"));
    }
    
    @GetMapping("/list")
    public ResponseEntity<Object> getDaynoteList(@AuthenticationPrincipal User user) {
        Map<Integer, List<DaynoteResponse>> daynoteListResponse = daynoteService.getDaynoteList(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("데이노트 리스트 조회", daynoteListResponse));
    }

}
