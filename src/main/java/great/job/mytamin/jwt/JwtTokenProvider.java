package great.job.mytamin.jwt;

import great.job.mytamin.service.CustomUserDetailsService;
import great.job.mytamin.domain.User;
import great.job.mytamin.exception.MytaminException;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import java.util.Base64;
import java.util.Date;

import static great.job.mytamin.exception.ErrorMap.INVALID_TOKEN_ERROR;

@RequiredArgsConstructor
@Component
public class JwtTokenProvider {

    private final CustomUserDetailsService customUserDetailsService;

    @Value("${jwt.secret.key}")
    private String secretKey;

    @Value("${jwt.access.token.valid.time}")
    private long accessTokenValidTime;

    @Value("${jwt.refresh.token.valid.time}")
    private long refreshTokenValidTime;

    @PostConstruct
    protected void init() {
        secretKey = Base64.getEncoder().encodeToString(secretKey.getBytes());
    }

    // JWT 토큰 생성
    public String createAccessToken(String email) {
        return createToken(email, accessTokenValidTime);
    }

    public String createRefreshToken(User user) {
        String refreshToken = createToken(user.getEmail(), refreshTokenValidTime);
        user.updateRefreshToken(refreshToken);
        return refreshToken;
    }

    public String createToken(String email, Long tokenValidTime) {
        Claims claims = Jwts.claims().setSubject(email);
        Date now = new Date();
        return Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(now)
                .setExpiration(new Date(now.getTime() + tokenValidTime))
                .signWith(SignatureAlgorithm.HS256, secretKey)
                .compact();
    }

    // JWT 토큰에서 인증 정보 조회
    public Authentication getAuthetication(String token) {
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(this.getUserPk(token));
        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    // 토큰에서 회원 정보 추출
    public String getUserPk(String token) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token).getBody().getSubject();
    }

    // Request의 Header에서 token 값을 가져온다 => "X-AUTH-TOKEN" : "TOKEN값"
    public String resolveToken(HttpServletRequest request) {
        return request.getHeader("X-AUTH-TOKEN");
    }

    // 토큰 유효성 체크 + 만료 기간 확인
    public boolean validateToken(String token) {
        try {
            Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
            return !claims.getBody().getExpiration().before(new Date());
        } catch (ExpiredJwtException e) { // 만료된 토큰
            return false;
        } catch (Exception e) { // 잘못된 토큰
            throw new MytaminException(INVALID_TOKEN_ERROR);
        }
    }

    // 토큰 만료 기간 계산
    public Long calValidTime(String token) {
        Date now = new Date();
        Jws<Claims> claims = Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token);
        return claims.getBody().getExpiration().getTime() - now.getTime();
    }

}