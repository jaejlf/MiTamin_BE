package great.job.mytamin.domain.user.repository;

import great.job.mytamin.domain.user.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
}
