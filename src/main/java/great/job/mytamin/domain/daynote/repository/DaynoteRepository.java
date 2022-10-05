package great.job.mytamin.domain.daynote.repository;

import great.job.mytamin.domain.daynote.entity.Daynote;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DaynoteRepository extends JpaRepository<Daynote, Long> {
}
