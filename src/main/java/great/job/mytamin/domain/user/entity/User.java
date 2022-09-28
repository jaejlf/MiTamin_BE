package great.job.mytamin.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.EAGER;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Email
    @Column(nullable = false, unique = true)
    private String email;

    @Column(length = 100, nullable = false)
    private String password;

    @Column(length = 300, nullable = false, unique = true)
    private String nickname;

    @Column(length = 300)
    private String profileImgUrl = "";

    @Column(length = 300)
    private String beMyMessage = "";

    @Column(length = 300)
    private String refreshToken = "";

    /*
    마이타민 섭취 지정 시간
    */
    private String mytaminHour;
    private String mytaminMin;

    /*
    행동 1 : 숨 고르기, 행동 2 : 감각 깨우기 실천 시간
    */
    private LocalDateTime breathTime = LocalDateTime.of(1999, 1, 1, 0, 0);
    private LocalDateTime senseTime = LocalDateTime.of(1999, 1, 1, 0, 0);
    
    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @JsonIgnore
    private Set<Mytamin> mytaminSet = new HashSet<>();

    public User(String email, String encodedPw, String nickname, String mytaminHour, String mytaminMin) {
        this.email = email;
        this.password = encodedPw;
        this.nickname = nickname;
        this.mytaminHour = mytaminHour;
        this.mytaminMin = mytaminMin;
        this.roles = Collections.singletonList("ROLE_USER");
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateBreathTime() {
        this.breathTime = LocalDateTime.now();
    }

    public void updateSenseTime() {
        this.senseTime = LocalDateTime.now();
    }

    public void updateMytaminTime(String mytaminHour, String mytaminMin) {
        this.mytaminHour = mytaminHour;
        this.mytaminMin = mytaminMin;
    }

    /*
    UserDetails Method
    */
    @ElementCollection(fetch = EAGER)
    private List<String> roles = new ArrayList<>();

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
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
