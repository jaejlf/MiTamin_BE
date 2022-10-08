package great.job.mytamin.topic.user.repository;

import great.job.mytamin.topic.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    Optional<User> findByRefreshToken(String refreshToken);
    boolean existsByNickname(String nickname);
}
