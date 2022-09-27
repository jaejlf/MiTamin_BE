package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    @GetMapping("/info/nickname")
    public String health(@AuthenticationPrincipal User user) {
        return user.getNickname();
    }

}
