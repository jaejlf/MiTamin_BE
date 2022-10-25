package great.job.mytamin.domain.mytamin.repository;

import great.job.mytamin.domain.mytamin.entity.Report;
import great.job.mytamin.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface ReportRepository extends JpaRepository<Report, Long> {
    List<Report> findAllByUserAndTakeAtBetween(User user, LocalDateTime start, LocalDateTime end);
    Optional<Report> findByUserAndReportId(User user, Long reportId);
}