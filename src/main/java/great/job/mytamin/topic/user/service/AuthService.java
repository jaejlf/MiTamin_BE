package great.job.mytamin.topic.user.service;

import great.job.mytamin.topic.user.dto.request.LoginRequest;
import great.job.mytamin.topic.user.dto.request.ReissueRequest;
import great.job.mytamin.topic.user.dto.request.SignUpRequest;
import great.job.mytamin.topic.user.dto.response.TokenResponse;
import great.job.mytamin.topic.user.dto.response.UserResponse;
import great.job.mytamin.topic.user.entity.User;
import great.job.mytamin.topic.user.enumerate.Provider;
import great.job.mytamin.topic.user.repository.UserRepository;
import great.job.mytamin.topic.util.UserUtil;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.jwt.JwtTokenProvider;
import great.job.mytamin.topic.util.MydayUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static great.job.mytamin.global.exception.ErrorMap.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomUserDetailsService customUserDetailsService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserUtil userUtil;
    private final MydayUtil mydayUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
    회원가입
    */
    @Transactional
    public UserResponse signup(SignUpRequest signUpRequest) {
        String email = signUpRequest.getEmail();
        String nickname = signUpRequest.getNickname();
        String password = signUpRequest.getPassword();
        validateRequest(email, nickname, password);

        User user = new User(
                email,
                passwordEncoder.encode(password),
                nickname,
                Provider.DEFAULT,
                signUpRequest.getMytaminHour(),
                signUpRequest.getMytaminMin(),
                isMytaminAlarmOn(signUpRequest.getMytaminHour())
        );
        user.updateDateOfMyday(mydayUtil.randomizeDateOfMyday()); // 마이데이 날짜 랜덤 지정
        return UserResponse.of(userRepository.save(user));
    }

    /*
    기본 로그인
    */
    @Transactional
    public TokenResponse defaultLogin(LoginRequest loginRequest) {
        User user = findUserByEmail(loginRequest.getEmail());
        checkPasswordMatching(loginRequest.getPassword(), user.getPassword());

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user);

        return TokenResponse.of(accessToken, refreshToken);
    }

    /*
    토큰 재발급
    */
    @Transactional
    public TokenResponse tokenReIssue(ReissueRequest reissueRequest) {
        User user = findUserByEmail(reissueRequest.getEmail());
        String refreshToken = reissueRequest.getRefreshToken();

        validateRefreshToken(refreshToken, user);

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());

        // 토큰 만료 기간이 2일 이내로 남았을 경우 refreshToken도 재발급
        if (jwtTokenProvider.calValidTime(refreshToken) <= 172800000) {
            refreshToken = jwtTokenProvider.createRefreshToken(user);
        }

        return TokenResponse.of(accessToken, refreshToken);
    }

    private void checkPasswordMatching(String requestPw, String realPw) {
        if (!passwordEncoder.matches(requestPw, realPw)) {
            throw new MytaminException(PASSWORD_MISMATCH_ERROR);
        }
    }

    private User findUserByEmail(String email) {
        return customUserDetailsService.loadUserByUsername(email);
    }

    private void validateRequest(String email, String nickname, String password) {
        validateEmailPattern(email);
        validatePasswordPattern(password);

        if (userUtil.checkEmailDuplication(email)) throw new MytaminException(USER_ALREADY_EXIST_ERROR);
        if (userUtil.checkNicknameDuplication(nickname)) throw new MytaminException(NICKNAME_DUPLICATE_ERROR);
    }

    private void validateRefreshToken(String refreshToken, User user) {
        // DB에 저장된 refreshToken과 일치하는지 체크
        if (!user.getRefreshToken().equals(refreshToken)) {
            throw new MytaminException(INVALID_TOKEN_ERROR);
        }

        // 리프레쉬 유효성 체크
        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new MytaminException(INVALID_TOKEN_ERROR);
        }
    }

    private void validateEmailPattern(String email) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"; // XXX@XXX.XXX
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (!m.matches()) throw new MytaminException(EMAIL_PATTERN_ERROR);
    }

    private void validatePasswordPattern(String password) {
        String regex = "^(?=.*[0-9])(?=.*[A-Za-z]).{8,30}$"; // 영문, 숫자를 포함한 8 ~ 30자리
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        if (!m.matches() || password.contains(" ")) throw new MytaminException(PASSWORD_PATTERN_ERROR);
    }

    private Boolean isMytaminAlarmOn(String mytaminHour) {
        return mytaminHour != null;
    }

}
