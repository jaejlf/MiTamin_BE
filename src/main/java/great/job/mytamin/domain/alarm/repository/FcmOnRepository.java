package great.job.mytamin.domain.alarm.repository;

import great.job.mytamin.domain.alarm.entity.FcmOn;
import great.job.mytamin.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FcmOnRepository extends JpaRepository<FcmOn, Long> {
    List<FcmOn> findAllByMytaminWhen(String mytaminWhen);
    FcmOn findByFcmToken(String fcmToken);
    void deleteByFcmToken(String fcmToken);
    void deleteAllByUser(User user);
}