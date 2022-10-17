package great.job.mytamin.domain.user.controller;

import great.job.mytamin.domain.user.dto.request.EmailCheckRequest;
import great.job.mytamin.domain.user.dto.request.LoginRequest;
import great.job.mytamin.domain.user.dto.request.ReissueRequest;
import great.job.mytamin.domain.user.dto.request.SignUpRequest;
import great.job.mytamin.domain.user.dto.response.TokenResponse;
import great.job.mytamin.domain.user.dto.response.UserResponse;
import great.job.mytamin.domain.user.service.AuthService;
import great.job.mytamin.domain.user.service.EmailService;
import great.job.mytamin.domain.util.UserUtil;
import great.job.mytamin.global.dto.response.NoDataResponse;
import great.job.mytamin.global.dto.response.ResultResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import java.util.Map;

import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final UserUtil userUtil;
    private final EmailService emailService;

    @PostMapping("/signup")
    public ResponseEntity<Object> signUp(@RequestBody SignUpRequest signUpRequest) {
        UserResponse userResponse = authService.signUp(signUpRequest);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("회원 가입", userResponse));
    }

    @PostMapping("/default/login")
    public ResponseEntity<Object> defaultLogin(@RequestBody LoginRequest loginRequest) {
        TokenResponse tokenResponse = authService.defaultLogin(loginRequest);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("기본 로그인", tokenResponse));
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<Object> isEmailDuplicate(@PathVariable String email) {
        Boolean isDuplicate = userUtil.isEmailDuplicate(email);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("이메일 중복 체크", isDuplicate));
    }

    @GetMapping("/check/nickname/{nickname}")
    public ResponseEntity<Object> isNicknameDuplicate(@PathVariable String nickname) {
        Boolean isDuplicate = userUtil.isNicknameDuplicate(nickname);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("닉네임 중복 체크", isDuplicate));
    }

    @GetMapping("/reissue")
    public ResponseEntity<Object> reissueToken(@RequestBody ReissueRequest reissueRequest) {
        TokenResponse tokenResponse = authService.reissueToken(reissueRequest);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("토큰 재발급", tokenResponse));
    }

    @PostMapping("/code")
    public ResponseEntity<Object> sendAuthCode(@RequestBody Map<String, String> map) throws MessagingException {
        emailService.sendAuthCode(map.get("email"));
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("이메일 인증 코드 전송"));
    }

    @GetMapping("/code")
    public ResponseEntity<Object> confirmAuthCode(@RequestBody EmailCheckRequest emailCheckRequest) {
        boolean isValidate = emailService.confirmAuthCode(emailCheckRequest);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("이메일 인증 코드 확인", isValidate));
    }

}
