package great.job.mytamin.domain.mytamin.repository.custom;

import great.job.mytamin.domain.mytamin.dto.request.CareSearchFilter;
import great.job.mytamin.domain.mytamin.entity.Care;
import great.job.mytamin.domain.user.entity.User;

import java.util.List;

public interface CustomCareRepository {
    List<Care> searchCareHistory(User user, CareSearchFilter careSearchFilter);
}
