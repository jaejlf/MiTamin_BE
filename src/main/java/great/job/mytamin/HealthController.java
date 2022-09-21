package great.job.mytamin;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class HealthController {

    @GetMapping("/health")
    public String health() {
        return "💪💪 마이타민 프로젝트 건강한 상태 💪💪";
    }

}