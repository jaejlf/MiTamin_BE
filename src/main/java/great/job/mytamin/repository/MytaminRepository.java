package great.job.mytamin.repository;

import great.job.mytamin.domain.Mytamin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MytaminRepository extends JpaRepository<Mytamin, Long> {
}
