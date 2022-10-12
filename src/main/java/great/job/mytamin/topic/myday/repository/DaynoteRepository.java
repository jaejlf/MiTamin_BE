package great.job.mytamin.topic.myday.repository;

import great.job.mytamin.topic.myday.entity.Daynote;
import great.job.mytamin.topic.myday.entity.Wish;
import great.job.mytamin.topic.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface DaynoteRepository extends JpaRepository<Daynote, Long> {
    int countByWish(Wish wish);
    Optional<Daynote> findByUserAndYearAndMonth(User user, int year, int month);
}