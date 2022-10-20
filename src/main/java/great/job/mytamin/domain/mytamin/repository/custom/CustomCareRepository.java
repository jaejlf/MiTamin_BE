package great.job.mytamin.domain.mytamin.repository.custom;

import great.job.mytamin.domain.mytamin.dto.request.CareSearchFilter;
import great.job.mytamin.domain.mytamin.entity.Care;

import java.util.List;

public interface CustomCareRepository {
    List<Care> searchCareHistory(CareSearchFilter careSearchFilter);
}
