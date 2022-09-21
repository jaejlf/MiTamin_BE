package great.job.mytamin.Service;

import great.job.mytamin.Repository.UserRepository;
import great.job.mytamin.domain.User;
import great.job.mytamin.dto.request.LoginRequest;
import great.job.mytamin.dto.request.SignUpRequest;
import great.job.mytamin.dto.response.TokenResponse;
import great.job.mytamin.dto.response.UserResponse;
import great.job.mytamin.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static great.job.mytamin.exception.ErrorMessage.PASSWORD_ERROR;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;

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

    /*
    기본 로그인
    */
    @Transactional
    public TokenResponse defaultLogin(LoginRequest loginRequest) {
        User user = customUserDetailsService.loadUserByUsername(loginRequest.getEmail());
        checkPasswordMatching(loginRequest.getPassword(), user.getPassword());

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        return TokenResponse.of(accessToken, refreshToken);
    }

    //올바른 비밀번호인지 체크
    private void checkPasswordMatching(String requestPw, String realPw) {
        if (!passwordEncoder.matches(requestPw, realPw)) {
            throw new IllegalArgumentException(PASSWORD_ERROR.getMsg());
        }
    }

}
