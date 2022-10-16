package great.job.mytamin.topic.myday.repository;

import great.job.mytamin.topic.myday.entity.Daynote;
import great.job.mytamin.topic.myday.entity.Wish;
import great.job.mytamin.topic.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface DaynoteRepository extends JpaRepository<Daynote, Long> {
    int countByWish(Wish wish);
    List<Daynote> findByUser(User user);
    Optional<Daynote> findByUserAndRawPerformedAt(User user, LocalDateTime rawPerformedAt);
}