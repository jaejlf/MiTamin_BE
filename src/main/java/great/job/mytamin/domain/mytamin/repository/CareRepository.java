package great.job.mytamin.domain.mytamin.repository;

import great.job.mytamin.domain.mytamin.entity.Care;
import great.job.mytamin.domain.mytamin.repository.custom.CustomCareRepository;
import great.job.mytamin.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CareRepository extends JpaRepository<Care, Long>, CustomCareRepository {
    long countByUser(User user);
    Page<Care> findAllByUser(User user, Pageable pageable);
}
