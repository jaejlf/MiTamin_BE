package great.job.mytamin.global.support;

import great.job.mytamin.domain.care.repository.CareRepository;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.repository.MytaminRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CommonServiceTest {

    public MockMvc mockMvc;

    @Autowired
    public UserRepository userRepository;
    @Autowired
    public MytaminRepository mytaminRepository;
    @Autowired
    public CareRepository careRepository;

    // Mock Data
    public User user;
    public Mytamin mytamin;
    public String mockTakeAtNow;

    @BeforeEach
    public void setUp() {
        user = new User(
                "tester@mock.com",
                "{{ENCODED_PASSWORD}}",
                "테스터",
                null,
                null
        );
        userRepository.save(user);

        mytamin = new Mytamin(
                LocalDateTime.now(),
                convertToTakeAt(LocalDateTime.now()),
                user
        );
        mytaminRepository.save(mytamin);

        mockTakeAtNow = convertToTakeAt(LocalDateTime.now());
    }

    // TimeService.converToTakeAt
    private String convertToTakeAt(LocalDateTime target) {
        if (target.getHour() <= 4) target = target.minusDays(1);
        String dayOfWeek = target.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        return target.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "." + dayOfWeek;
    }

}
