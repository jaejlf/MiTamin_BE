package great.job.mytamin.global.support;

import great.job.mytamin.domain.care.entity.Care;
import great.job.mytamin.domain.care.repository.CareRepository;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.repository.MytaminRepository;
import great.job.mytamin.domain.report.entity.Report;
import great.job.mytamin.domain.report.enumerate.MentalCondition;
import great.job.mytamin.domain.report.repository.ReportRepository;
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

    @Autowired public UserRepository userRepository;
    @Autowired public MytaminRepository mytaminRepository;
    @Autowired public CareRepository careRepository;
    @Autowired public ReportRepository reportRepository;

    // Mock Data
    public User user;
    public Mytamin mytamin;
    public String mockTakeAtNow;
    public Report report;
    public Care care;

    @BeforeEach
    public void setUp() {

        // Mock User
        user = new User(
                "tester@mock.com",
                "{{ENCODED_PASSWORD}}",
                "테스터",
                null,
                null
        );
        userRepository.save(user);

        // Mock Mytamin
        mytamin = new Mytamin(
                LocalDateTime.now(),
                convertToTakeAt(LocalDateTime.now()),
                user
        );
        mytaminRepository.save(mytamin);

        // Mock Time
        mockTakeAtNow = convertToTakeAt(LocalDateTime.now());

        // Mock Report & Care
        report = new Report(
                MentalCondition.VERY_GOOD.getMsg(),
                "신나는",
                "즐거운",
                "재밌는",
                "아무래도 아침형 인간이 되는건 너무 어려운 것 같다.",
                mytamin
        );

        care = new Care(
                "이루어 낸 일",
                "오늘 할 일을 전부 했어",
                "성실히 노력하는 내 모습이 좋아",
                mytamin
        );
    }

    // TimeService.converToTakeAt
    private String convertToTakeAt(LocalDateTime target) {
        if (target.getHour() <= 4) target = target.minusDays(1);
        String dayOfWeek = target.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        return target.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "." + dayOfWeek;
    }

}
