package great.job.mytamin.global.controller;

import great.job.mytamin.domain.myday.service.MydayService;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.exception.MytaminException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static great.job.mytamin.global.exception.ErrorMap.UNAUTHORIZED_ERROR;
import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class AdminController {

    private final MydayService mydayService;

    @GetMapping("/init/myday")
    public ResponseEntity<Object> initMyday(@AuthenticationPrincipal User user) {
        if (!user.getRoles().contains("ROLE_ADMIN")) throw new MytaminException(UNAUTHORIZED_ERROR);
        mydayService.init();
        return ResponseEntity
                .status(CREATED)
                .body(NoDataResponse.create("마이데이 초기 데이터 생성"));
    }

}
