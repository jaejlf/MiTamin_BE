package great.job.mytamin.domain.mytamin.controller;

import great.job.mytamin.domain.mytamin.dto.request.CareRequest;
import great.job.mytamin.domain.mytamin.dto.request.CareSearchFilter;
import great.job.mytamin.domain.mytamin.dto.response.CareHistoryResponse;
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

import java.util.List;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/care")
public class CareController {

    private final CareService careService;

    @PostMapping("/new")
    public ResponseEntity<Object> createCare(@AuthenticationPrincipal User user,
                                             @RequestBody CareRequest request) {
        CareResponse result = careService.createCare(user, request);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("칭찬 처방", result));
    }

    @GetMapping("/{careId}")
    public ResponseEntity<Object> getCare(@AuthenticationPrincipal User user,
                                          @PathVariable Long careId) {
        CareResponse result = careService.getCare(user, careId);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok(careId + "번 칭찬 처방 조회", result));
    }

    @PutMapping("/{careId}")
    public ResponseEntity<Object> updateCare(@AuthenticationPrincipal User user,
                                             @PathVariable Long careId,
                                             @RequestBody CareRequest request) {
        careService.updateCare(user, careId, request);
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok(careId + "번 칭찬 처방 수정"));
    }

    @GetMapping("/random")
    public ResponseEntity<Object> getRandomCare(@AuthenticationPrincipal User user) {
        RandomCareResponse result = careService.getRandomCare(user);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("칭찬 처방 랜덤 조회", result));
    }

    @PostMapping("/list")
    public ResponseEntity<Object> getCareHistroy(@AuthenticationPrincipal User user,
                                                 @RequestBody CareSearchFilter filter) {
        Map<String, List<CareHistoryResponse>> result = careService.getCareHistroy(user, filter);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("칭찬 처방 히스토리", result));
    }

}