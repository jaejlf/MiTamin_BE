package great.job.mytamin.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Email
    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 300, nullable = false)
    private String password;

    @Column(length = 100, nullable = false, unique = true)
    private String nickname;

    @Column(length = 300)
    private String profileImgUrl = "";

    @Column(length = 300)
    private String beMyMessage = "";

    @Column(length = 300)
    private String refreshToken = "";

    /*
    마이타민 섭취 알림 시간
    */
    private String mytaminHour;
    private String mytaminMin;

    public User(String email, String encodedPw, String nickname, String mytaminHour, String mytaminMin) {
        this.email = email;
        this.password = encodedPw;
        this.nickname = nickname;
        this.mytaminHour = mytaminHour;
        this.mytaminMin = mytaminMin;
    }

    /*
    refreshToken 업데이트
    */
    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    /*
    UserDetails Method
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        return authorities;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return email;
    } // userPk -> loginId

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    } //계정 만료 여부 (true : 만료 X)

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    } //계정 잠김 여부 (true : 잠김 X)

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    } //비밀번호 만료 여부 (ture : 만료 X)

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    } //계정 활성화 여부 (true : 활성화)

}
