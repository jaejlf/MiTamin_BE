package great.job.mytamin.domain.mytamin.repository;

import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MytaminRepository extends JpaRepository<Mytamin, Long> {
    Optional<Mytamin> findByTakeAtAndUser(String takeAt, User user);
    Mytamin findFirstByUserOrderByMytaminIdDesc(User user);
}
