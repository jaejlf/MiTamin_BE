package great.job.mytamin.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(length = 100, nullable = false, unique = true)
    private String email;

    @Column(length = 300, nullable = false)
    private String password;

    @Column(length = 100, nullable = false)
    private String nickname;

    @Column(length = 300)
    private String profileImgUrl;

    @Column(length = 300)
    private String beMyMessage = "";

    //마이타민 섭취 알림 시간 (건너뛸 경우 null)
    private int mytaminHour;
    private int mytaminMin;

    public User(String email, String encodedPw, String nickname, int mytaminHour, int mytaminMin) {
        this.email = email;
        this.password = encodedPw;
        this.nickname = nickname;
        this.mytaminHour = mytaminHour;
        this.mytaminMin = mytaminMin;
    }

    /*
    UserDetails Method
    */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null;
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
