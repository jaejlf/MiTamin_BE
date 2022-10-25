package great.job.mytamin.domain.myday.repository;

import great.job.mytamin.domain.myday.entity.Daynote;
import great.job.mytamin.domain.myday.entity.Wish;
import great.job.mytamin.domain.myday.repository.custom.CustomDaynoteRepository;
import great.job.mytamin.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;

public interface DaynoteRepository extends JpaRepository<Daynote, Long>, CustomDaynoteRepository {
    int countByWish(Wish wish);
    Optional<Daynote> findByUserAndDaynoteId(User user, Long daynoteId);
    Optional<Daynote> findByUserAndPerformedAt(User user, LocalDateTime performedAt);
    void deleteAllByUser(User user);
}