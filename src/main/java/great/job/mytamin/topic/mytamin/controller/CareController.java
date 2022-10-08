package great.job.mytamin.topic.mytamin.controller;

import great.job.mytamin.topic.mytamin.dto.request.CareRequest;
import great.job.mytamin.topic.mytamin.dto.response.CareResponse;
import great.job.mytamin.topic.mytamin.service.CareService;
import great.job.mytamin.topic.user.entity.User;
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

    @PutMapping("/{careId}")
    public ResponseEntity<Object> updateCare(@AuthenticationPrincipal User user,
                                             @PathVariable Long careId,
                                             @RequestBody CareRequest careRequest) {
        careService.updateCare(user, careId, careRequest);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("칭찬 처방 수정 완료", null));
    }

}
