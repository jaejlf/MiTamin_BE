package great.job.mytamin.repository;

import great.job.mytamin.domain.Care;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareRepository extends JpaRepository<Care, Long> {
}
