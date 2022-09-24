package great.job.mytamin.jwt;

import great.job.mytamin.repository.UserRepository;
import great.job.mytamin.exception.MytaminException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static great.job.mytamin.exception.ErrorMap.EXPIRED_TOKEN_ERROR;
import static great.job.mytamin.exception.ErrorMap.INVALID_TOKEN_ERROR;

@Component
@RequiredArgsConstructor
public class JwtAuthInterceptor implements HandlerInterceptor {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserRepository userRepository;
    
    //컨트롤러 실행 전 수행 (true -> 컨트롤러로 진입, false -> 진입 X)
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response, Object handler) {
        
        String token = jwtTokenProvider.resolveToken(request);
        
        // 액세스 토큰 유효성 체크
        if (!jwtTokenProvider.validateToken(token)) {
            throw new MytaminException(EXPIRED_TOKEN_ERROR);
        }

        // 리프레쉬 토큰인지 확인
        if (userRepository.findByRefreshToken(token).isPresent()) {
            throw new MytaminException(INVALID_TOKEN_ERROR);
        }

        return true;
    }

}
