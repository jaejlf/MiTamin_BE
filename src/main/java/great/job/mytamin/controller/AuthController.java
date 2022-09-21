package great.job.mytamin.controller;

import great.job.mytamin.Service.UserService;
import great.job.mytamin.dto.request.SignUpRequest;
import great.job.mytamin.dto.response.ResultResponse;
import great.job.mytamin.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.http.HttpStatus.CREATED;

@RestController
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final UserService userService;

    /*
    회원가입
    */
    @PostMapping("/signup")
    public ResponseEntity<Object> signup(@RequestBody SignUpRequest signUpRequest) {
        UserResponse newUser = userService.signup(signUpRequest);
        return ResponseEntity
                .status(CREATED)
                .body(ResultResponse.create("회원 가입 성공", newUser));
    }

}