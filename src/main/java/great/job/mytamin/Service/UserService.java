package great.job.mytamin.Service;

import great.job.mytamin.Repository.UserRepository;
import great.job.mytamin.domain.User;
import great.job.mytamin.dto.request.SignUpRequest;
import great.job.mytamin.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
    회원가입
    */
    @Transactional
    public UserResponse signup(SignUpRequest signUpRequest) {
        User user = new User(
                signUpRequest.getEmail(),
                passwordEncoder.encode(signUpRequest.getPassword()),
                signUpRequest.getNickname(),
                signUpRequest.getMytaminHour(),
                signUpRequest.getMytaminMin()
        );
        return UserResponse.of(userRepository.save(user));
    }

}
