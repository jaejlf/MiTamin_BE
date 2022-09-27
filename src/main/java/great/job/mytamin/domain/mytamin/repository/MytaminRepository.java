package great.job.mytamin.domain.mytamin.repository;

import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MytaminRepository extends JpaRepository<Mytamin, Long> {
    Mytamin findByTakeAtAndUser(String takeMytaminAtStr, User user);
    Mytamin findFirstByUserOrderByMytaminIdDesc(User user);
}
