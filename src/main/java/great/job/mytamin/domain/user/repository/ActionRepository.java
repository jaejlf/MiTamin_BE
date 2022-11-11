package great.job.mytamin.domain.user.repository;

import great.job.mytamin.domain.user.entity.Action;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepository extends JpaRepository<Action, Long> {
}
