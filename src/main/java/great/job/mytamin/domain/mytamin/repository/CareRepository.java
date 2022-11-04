package great.job.mytamin.domain.mytamin.repository;

import great.job.mytamin.domain.mytamin.entity.Care;
import great.job.mytamin.domain.mytamin.repository.custom.CustomCareRepository;
import great.job.mytamin.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CareRepository extends JpaRepository<Care, Long>, CustomCareRepository {
    long countByUser(User user);
    Page<Care> findAllByUser(User user, Pageable pageable);
    Optional<Care> findByUserAndCareId(User user, Long careId);
    List<Care> findAllByUser(User user);
}
