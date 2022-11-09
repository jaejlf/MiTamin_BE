package great.job.mytamin.global.controller;

import great.job.mytamin.global.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/health")
public class HealthController {

    private final RedisService redisService;

//    @Value("${spring.profiles.active}")
//    private String profile;

    @GetMapping
    public String health() {
        return "💪💪 마이타민 프로젝트 건강한 상태 💪💪";
    }

    @GetMapping("/redis/{email}")
    public String redisCheck(@PathVariable String email) {
        return redisService.getValues(email);
    }

//    @GetMapping("/profile")
//    public String getCurProfile() {
//        return profile;
//    }

}