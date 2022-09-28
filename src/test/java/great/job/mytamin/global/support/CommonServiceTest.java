package great.job.mytamin.global.support;

import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.mytamin.repository.MytaminRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

@SpringBootTest
@Transactional
public class CommonServiceTest {

    @Autowired public UserRepository userRepository;
    @Autowired public MytaminRepository mytaminRepository;

    // Mock Data
    public String TESER1_ACCESS_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0ZXIxQG1vY2suY29tIiwiaWF0IjoxNjY0MzMxMzY3LCJleHAiOjE2Nzk4ODMzNjd9.03LzMTyHIcspNluSK7EBG_HJzbo3c6Iy41QsxhEJX-I";
    public String TESTER1_REFRESH_TOKEN = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0ZXN0ZXIxQG1vY2suY29tIiwiaWF0IjoxNjY0MzMxMzY3LCJleHAiOjE2OTU0MzUzNjd9.m2NGZTsa4ddxHMOL0UCGi-4Uz9Qsz04WK-t081of5Qo";

    public User user;
    public Mytamin mytamin;

    @BeforeEach
    public void setUp() {

        // Mock User
        user = new User(
                "tester1@mock.com",
                "{{ENCODED_PASSWORD}}",
                "테스터1",
                "22",
                "00"
        );

        // Mock Mytamin
        LocalDateTime rawTakeAt = LocalDateTime.now();
        String dayOfWeek = rawTakeAt.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        String takeAt = rawTakeAt.format(DateTimeFormatter.ofPattern("yyyy.MM.dd")) + "." + dayOfWeek;
        mytamin = new Mytamin(
                rawTakeAt,
                takeAt,
                user
        );
        mytaminRepository.save(mytamin);

    }

}
