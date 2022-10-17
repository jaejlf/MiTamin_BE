package great.job.mytamin.domain.util;

import great.job.mytamin.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserUtil {

    private final UserRepository userRepository;

    /*
    이메일 중복 체크 (true : 이미 사용 중, false : 사용 가능)
    */
    @Transactional(readOnly = true)
    public Boolean checkEmailDuplication(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /*
    닉네임 중복 체크 (true : 이미 사용 중, false : 사용 가능)
    */
    @Transactional(readOnly = true)
    public Boolean checkNicknameDuplication(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

}
