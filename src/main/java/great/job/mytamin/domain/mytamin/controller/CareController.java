package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.dto.request.CareRequest;
import great.job.mytamin.domain.mytamin.dto.request.CareSearchFilter;
import great.job.mytamin.domain.mytamin.dto.response.CareHistoryListResponse;
import great.job.mytamin.domain.mytamin.dto.response.CareResponse;
import great.job.mytamin.domain.mytamin.dto.response.RandomCareResponse;
import great.job.mytamin.domain.mytamin.service.CareService;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/care")
public class CareController {

    private final CareService careService;

    @PostMapping("/new")
    public ResponseEntity<Object> createCare(@AuthenticationPrincipal User user,
                                             @RequestBody CareRequest careRequest) {
        CareResponse careResponse = careService.createCare(user, careRequest);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("칭찬 처방하기", careResponse));
    }

    @GetMapping("/{careId}")
    public ResponseEntity<Object> getCare(@PathVariable Long careId) {
        CareResponse careResponse = careService.getCare(careId);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("칭찬 처방 조회", careResponse));
    }

    @PutMapping("/{careId}")
    public ResponseEntity<Object> updateCare(@AuthenticationPrincipal User user,
                                             @PathVariable Long careId,
                                             @RequestBody CareRequest careRequest) {
        careService.updateCare(user, careId, careRequest);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("칭찬 처방 수정 완료"));
    }

    @GetMapping("/random")
    public ResponseEntity<Object> getRandomCare(@AuthenticationPrincipal User user) {
        RandomCareResponse randomCareResponse = careService.getRandomCare(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("칭찬 처방 랜덤 조회", randomCareResponse));
    }

    @GetMapping("/list")
    public ResponseEntity<Object> getCareHistroy(@AuthenticationPrincipal User user,
                                                 @RequestBody CareSearchFilter careSearchFilter) {
        CareHistoryListResponse careHistoryListResponse = careService.getCareHistroy(user, careSearchFilter);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("칭찬 처방 히스토리 조회", careHistoryListResponse));
    }

}
