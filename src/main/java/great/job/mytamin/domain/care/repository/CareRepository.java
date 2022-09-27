package great.job.mytamin.domain.care.repository;

import great.job.mytamin.domain.care.entity.Care;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareRepository extends JpaRepository<Care, Long> {
}
