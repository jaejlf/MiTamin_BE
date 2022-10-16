package great.job.mytamin.topic.myday.controller;

import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import great.job.mytamin.topic.myday.dto.request.DaynoteRequest;
import great.job.mytamin.topic.myday.dto.request.DaynoteUpdateRequest;
import great.job.mytamin.topic.myday.dto.response.DaynoteListResponse;
import great.job.mytamin.topic.myday.dto.response.DaynoteResponse;
import great.job.mytamin.topic.myday.service.DaynoteService;
import great.job.mytamin.topic.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

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

    @PostMapping("/new")
    public ResponseEntity<Object> createDaynote(@AuthenticationPrincipal User user,
                                                @RequestPart(value = "fileList", required = false) List<MultipartFile> fileList,
                                                @RequestPart(value = "dayNote") DaynoteRequest daynoteRequest) {
        DaynoteResponse daynoteResponse = daynoteService.createDaynote(user, fileList, daynoteRequest);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("데이노트 작성하기", daynoteResponse));
    }

    @GetMapping("/{daynoteId}")
    public ResponseEntity<Object> getDaynote(@PathVariable Long daynoteId) {
        DaynoteResponse daynoteResponse = daynoteService.getDaynote(daynoteId);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("데이노트 조회", daynoteResponse));
    }

    @PutMapping("/{daynoteId}")
    public ResponseEntity<Object> updateDaynote(@AuthenticationPrincipal User user,
                                                @RequestPart(value = "fileList", required = false) List<MultipartFile> fileList,
                                                @PathVariable Long daynoteId,
                                                @RequestPart(value = "dayNote") DaynoteUpdateRequest daynoteUpdateRequest) {
        daynoteService.updateDaynote(user, fileList, daynoteId, daynoteUpdateRequest);
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
        DaynoteListResponse daynoteListResponse = daynoteService.getDaynoteList(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("데이노트 리스트 조회", daynoteListResponse));
    }

}
