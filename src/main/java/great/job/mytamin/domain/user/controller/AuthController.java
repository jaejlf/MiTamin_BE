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
import javax.validation.Valid;
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
    public ResponseEntity<Object> signUp(@RequestBody @Valid SignUpRequest request) {
        UserResponse result = authService.signUp(request);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("회원 가입", result));
    }

    @PostMapping("/default/login")
    public ResponseEntity<Object> defaultLogin(@RequestBody @Valid LoginRequest request) {
        TokenResponse result = authService.defaultLogin(request);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("기본 로그인", result));
    }

    @GetMapping("/check/email/{email}")
    public ResponseEntity<Object> isEmailDuplicate(@PathVariable String email) {
        Boolean result = userUtil.isEmailDuplicate(email);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("이메일 중복 체크", result));
    }

    @GetMapping("/check/nickname/{nickname}")
    public ResponseEntity<Object> isNicknameDuplicate(@PathVariable String nickname) {
        Boolean result = userUtil.isNicknameDuplicate(nickname);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("닉네임 중복 체크", result));
    }

    @PostMapping("/reissue")
    public ResponseEntity<Object> reissueToken(@RequestBody @Valid ReissueRequest request) {
        TokenResponse result = authService.reissueToken(request);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("토큰 재발급", result));
    }

    @PostMapping("/signup/code")
    public ResponseEntity<Object> sendAuthCodeForSignUp(@RequestBody Map<String, String> request) throws MessagingException {
        emailService.sendAuthCodeForSignUp(request.get("email"));
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("회원가입을 위한 이메일 인증"));
    }

    @PostMapping("/reset/code")
    public ResponseEntity<Object> sendAuthCodeForResetPW(@RequestBody Map<String, String> request) throws MessagingException {
        authService.sendAuthCodeForResetPW(request.get("email"));
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("비밀번호 재설정을 위한 이메일 인증"));
    }

    @PostMapping("/code")
    public ResponseEntity<Object> confirmAuthCode(@RequestBody @Valid EmailCheckRequest request) {
        boolean result = emailService.confirmAuthCode(request);
        return ResponseEntity
                .status(OK)
                .body(ResultResponse.ok("이메일 인증 코드 확인", result));
    }

    @PutMapping("/password")
    public ResponseEntity<Object> resetPassword(@RequestBody Map<String, String> request) {
        authService.resetPassword(request.get("email"), request.get("password"));
        return ResponseEntity
                .status(OK)
                .body(NoDataResponse.ok("비밀번호 재설정"));
    }

}
