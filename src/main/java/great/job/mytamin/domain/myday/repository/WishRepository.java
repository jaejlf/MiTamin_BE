package great.job.mytamin.domain.myday.repository;

import great.job.mytamin.domain.myday.entity.Wish;
import great.job.mytamin.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface WishRepository extends JpaRepository<Wish, Long> {
    List<Wish> findAllByUserAndIsHidden(User user, Boolean isHidden);
    Optional<Wish> findByUserAndWishText(User user, String wishText);
    void deleteAllByUser(User user);
}