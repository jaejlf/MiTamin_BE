package great.job.mytamin.global.support;

import great.job.mytamin.domain.mytamin.repository.MytaminRepository;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class CommonServiceTest {

    @Autowired public UserRepository userRepository;
    @Autowired public MytaminRepository mytaminRepository;

    // Mock Data
    public User user;

    @BeforeEach
    public void setUp() {
        // Mock User
        user = new User(
                "mytamin@naver.com",
                "{{ENCODED_PASSWORD}}",
                "강철멘탈",
                "22",
                "00"
        );
        userRepository.save(user);
    }

}
