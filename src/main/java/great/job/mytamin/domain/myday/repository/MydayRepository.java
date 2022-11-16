package great.job.mytamin.domain.myday.repository;

import great.job.mytamin.domain.myday.entity.Myday;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MydayRepository extends JpaRepository<Myday, Long> {
    Myday findByMydayId(Long mydayId);
}
