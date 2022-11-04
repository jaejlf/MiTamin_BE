package great.job.mytamin.domain.util;

import great.job.mytamin.domain.user.repository.UserRepository;
import great.job.mytamin.global.exception.MytaminException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static great.job.mytamin.global.exception.ErrorMap.EMAIL_PATTERN_ERROR;
import static great.job.mytamin.global.exception.ErrorMap.PASSWORD_PATTERN_ERROR;

@Service
@RequiredArgsConstructor
public class UserUtil {

    private final UserRepository userRepository;

    /*
    이메일 중복 체크 (true : 이미 사용 중, false : 사용 가능)
    */
    @Transactional(readOnly = true)
    public Boolean isEmailDuplicate(String email) {
        return userRepository.findByEmail(email).isPresent();
    }

    /*
    닉네임 중복 체크 (true : 이미 사용 중, false : 사용 가능)
    */
    @Transactional(readOnly = true)
    public Boolean isNicknameDuplicate(String nickname) {
        return userRepository.existsByNickname(nickname);
    }

    /*
    이메일 패턴 체크
    */
    public void validateEmailPattern(String email) {
        String regex = "^[_a-z0-9-]+(.[_a-z0-9-]+)*@(?:\\w+\\.)+\\w+$"; // XXX@XXX.XXX
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(email);
        if (!m.matches()) throw new MytaminException(EMAIL_PATTERN_ERROR);
    }

    /*
    비밀번호 패턴 체크
    */
    public void validatePasswordPattern(String password) {
        String regex = "^(?=.*[0-9])(?=.*[A-Za-z]).{8,30}$"; // 영문, 숫자를 포함한 8 ~ 30자리
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(password);
        if (!m.matches() || password.contains(" ")) throw new MytaminException(PASSWORD_PATTERN_ERROR);
    }
    
}
