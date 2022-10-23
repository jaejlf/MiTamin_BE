package great.job.mytamin.domain.myday.repository.custom;

import great.job.mytamin.domain.myday.entity.Daynote;
import great.job.mytamin.domain.user.entity.User;

import java.util.List;

public interface CustomDaynoteRepository {
    List<Daynote> searchDaynoteList(User user);
}
