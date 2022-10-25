package great.job.mytamin.domain.mytamin.repository;

import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface MytaminRepository extends JpaRepository<Mytamin, Long> {
    Optional<Mytamin> findByUserAndTakeAt(User user, LocalDateTime takeAt);
    Mytamin findFirstByUserOrderByMytaminIdDesc(User user);
    List<Mytamin> findAllByUserAndTakeAtBetween(User user, LocalDateTime start, LocalDateTime end);
    void deleteAllByUser(User user);
    Optional<Mytamin> findByUserAndMytaminId(User user, Long mytaminId);
}
