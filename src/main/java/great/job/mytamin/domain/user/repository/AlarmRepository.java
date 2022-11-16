package great.job.mytamin.domain.user.repository;

import great.job.mytamin.domain.user.entity.Alarm;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AlarmRepository extends JpaRepository<Alarm, Long> {
    List<Alarm> findByMytaminAlarmOnAndMytaminHourAndMytaminMin(Boolean mytaminAlarmOn, String mytaminHour, String mytaminMin);
    List<Alarm> findByMydayAlarmOnAndMydayWhen(Boolean mydayAlarmOn, String mydayWhen);
}
