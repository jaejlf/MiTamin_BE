package great.job.mytamin.topic.myday.repository;

import great.job.mytamin.topic.myday.entity.Daynote;
import great.job.mytamin.topic.myday.entity.Wish;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DaynoteRepository extends JpaRepository<Daynote, Long> {
    int countByWish(Wish wish);
}