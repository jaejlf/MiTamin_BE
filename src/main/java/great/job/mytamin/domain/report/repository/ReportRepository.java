package great.job.mytamin.domain.report.repository;

import great.job.mytamin.domain.report.entity.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report, Long> {
}
