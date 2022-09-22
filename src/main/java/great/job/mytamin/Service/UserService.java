package great.job.mytamin.Service;

import great.job.mytamin.Repository.UserRepository;
import great.job.mytamin.domain.User;
import great.job.mytamin.dto.request.LoginRequest;
import great.job.mytamin.dto.request.SignUpRequest;
import great.job.mytamin.dto.response.TokenResponse;
import great.job.mytamin.dto.response.UserResponse;
import great.job.mytamin.exception.MytaminException;
import great.job.mytamin.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static great.job.mytamin.exception.ErrorMap.*;

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

        // 이메일 & 닉네임 유효성 체크 & 중복 체크
        String email = signUpRequest.getEmail();
        String nickname = signUpRequest.getNickname();
        validateEmailPattern(email);
        if (checkEmailDuplication(email)) throw new MytaminException(USER_ALREADY_EXIST_ERROR);
        if (checkNicknameDuplication(nickname)) throw new MytaminException(NICKNAME_DUPLICATE_ERROR);

        // 새로운 유저 저장
        User user = new User(
                email,
                passwordEncoder.encode(signUpRequest.getPassword()),
                nickname,
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
        User user = getUserByEmail(loginRequest.getEmail());
        checkPasswordMatching(loginRequest.getPassword(), user.getPassword());

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getEmail());

        return TokenResponse.of(accessToken, refreshToken);
    }

    /*
    이메일 중복 체크 (true : 이미 사용 중, false : 사용 가능)
    */
    @Transactional(readOnly = true)
    public boolean checkEmailDuplication(String email) {
        if (userRepository.findByEmail(email).isPresent()) {
            return true;
        } else return false;
    }

    /*
    닉네임 중복 체크 (true : 이미 사용 중, false : 사용 가능)
    */
    @Transactional(readOnly = true)
    public boolean checkNicknameDuplication(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    // 올바른 비밀번호인지 체크
    private void checkPasswordMatching(String requestPw, String realPw) {
        if (!passwordEncoder.matches(requestPw, realPw)) {
            throw new MytaminException(PASSWORD_MISMATCH_ERROR);
        }
    }

    // 이메일로 유저 정보 찾기
    private User getUserByEmail(String email) {
        return customUserDetailsService.loadUserByUsername(email);
    }

    // 이메일 형식 체크
    private void validateEmailPattern(String email) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (!m.matches()) throw new MytaminException(EMAIL_PATTERN_ERROR);
    }

}
