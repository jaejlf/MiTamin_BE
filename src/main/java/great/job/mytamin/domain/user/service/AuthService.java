package great.job.mytamin.domain.user.service;

import great.job.mytamin.domain.user.dto.request.LoginRequest;
import great.job.mytamin.domain.user.dto.request.ReissueRequest;
import great.job.mytamin.domain.user.dto.request.SignUpRequest;
import great.job.mytamin.domain.user.dto.response.TokenResponse;
import great.job.mytamin.domain.user.dto.response.UserResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.enumerate.Provider;
import great.job.mytamin.domain.user.repository.UserRepository;
import great.job.mytamin.domain.util.UserUtil;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static great.job.mytamin.global.exception.ErrorMap.*;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final CustomUserDetailsService customUserDetailsService;
    private final EmailService emailService;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserUtil userUtil;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /*
    회원가입
    */
    @Transactional
    public UserResponse signUp(SignUpRequest request) {
        validateRequest(request);
        User user = new User(
                request.getEmail(),
                passwordEncoder.encode(request.getPassword()),
                request.getNickname(),
                Provider.DEFAULT
        );
        return UserResponse.of(userRepository.save(user));
    }

    /*
    기본 로그인
    */
    @Transactional
    public TokenResponse defaultLogin(LoginRequest request) {
        User user = findUserByEmail(request.getEmail());
        checkPasswordMatching(request.getPassword(), user.getPassword());
        
        // 토큰 업데이트
        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());
        String refreshToken = jwtTokenProvider.createRefreshToken(user);
        
        // DB 업데이트
        user.updateRefreshToken(refreshToken);
        userRepository.save(user);

        return TokenResponse.of(accessToken, refreshToken);
    }

    /*
    토큰 재발급
    */
    @Transactional
    public TokenResponse reissueToken(ReissueRequest request) {
        User user = findUserByEmail(request.getEmail());
        String refreshToken = request.getRefreshToken();

        validateRefreshToken(refreshToken, user);

        String accessToken = jwtTokenProvider.createAccessToken(user.getEmail());

        // 토큰 만료 기간이 2일 이내로 남았을 경우 refreshToken도 재발급
        if (jwtTokenProvider.calValidTime(refreshToken) <= 172800000) {
            refreshToken = jwtTokenProvider.createRefreshToken(user);
        }

        return TokenResponse.of(accessToken, refreshToken);
    }

    /*
    비밀번호 재설정을 위한 이메일 인증 코드 전송
    */
    public void sendAuthCodeForResetPW(String email) throws MessagingException {
        emailService.sendAuthCodeForSignUp(email);
    }

    /*
    비밀번호 변경(또는 재설정)
    */
    public void resetPassword(String email, String password) {
        User user = findUserByEmail(email);
        user.updatePassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    private User findUserByEmail(String email) {
        return customUserDetailsService.loadUserByUsername(email);
    }

    private void validateRequest(SignUpRequest request) {
        validateEmailPattern(request.getEmail());
        validatePasswordPattern(request.getPassword());

        if (userUtil.isEmailDuplicate(request.getEmail())) throw new MytaminException(USER_ALREADY_EXIST_ERROR);
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

    private void checkPasswordMatching(String requestPw, String realPw) {
        if (!passwordEncoder.matches(requestPw, realPw)) {
            throw new MytaminException(PASSWORD_MISMATCH_ERROR);
        }
    }

}
