package great.job.mytamin.domain.care.controller;

import great.job.mytamin.domain.care.dto.request.CareRequest;
import great.job.mytamin.domain.care.dto.response.CareResponse;
import great.job.mytamin.domain.care.service.CareService;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

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

}
