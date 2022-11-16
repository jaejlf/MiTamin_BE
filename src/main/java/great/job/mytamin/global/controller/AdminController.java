package great.job.mytamin.global.controller;

import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.service.AdminService;
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

    private final AdminService adminService;

    @GetMapping("/init/myday")
    public ResponseEntity<Object> initMyday(@AuthenticationPrincipal User user) {
        if (!user.getRoles().contains("ROLE_ADMIN")) throw new MytaminException(UNAUTHORIZED_ERROR);
        adminService.initMyday();
        return ResponseEntity
                .status(CREATED)
                .body(NoDataResponse.create("마이데이 초기 데이터 생성"));
    }

//    @GetMapping("/fcm/push/list")
//    public ResponseEntity<Object> getPusyList(@AuthenticationPrincipal User user) throws IOException {
//        if (!user.getRoles().contains("ROLE_ADMIN")) throw new MytaminException(UNAUTHORIZED_ERROR);
//        adminService.getMytaminPushList();
//        adminService.getMydayPushList();
//        return ResponseEntity
//                .status(CREATED)
//                .body(NoDataResponse.create("푸시 알림 리스트 테스트"));
//    }

}
