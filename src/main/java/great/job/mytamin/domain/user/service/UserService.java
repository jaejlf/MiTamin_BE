package great.job.mytamin.domain.user.service;

import great.job.mytamin.domain.user.dto.response.ProfileResponse;
import great.job.mytamin.domain.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    /*
    마이페이지 프로필 조회
    */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(User user) {
        return ProfileResponse.of(user);
    }
    
}
