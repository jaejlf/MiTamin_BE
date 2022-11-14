package great.job.mytamin.domain.user.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.user.enumerate.Provider;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import javax.validation.constraints.Email;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static javax.persistence.FetchType.LAZY;

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

    @Column(nullable = false)
    private String password;

    @Length(min = 1, max = 9)
    @Column(nullable = false)
    private String nickname;

    @Length(max = 300)
    private String profileImgUrl;

    @Length(min = 1, max = 20)
    private String beMyMessage = "마음 면역력이 높아질";

    private String refreshToken = "";
    private LocalDateTime createdAt = LocalDateTime.now();

    @ElementCollection
    private List<String> fcmTokenList = new ArrayList<>();

    @Enumerated(EnumType.STRING)
    private Provider provider;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "actionId")
    private Action action;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "alarmId")
    private Alarm alarm;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @JsonIgnore
    private Set<Mytamin> mytaminSet = new HashSet<>();

    public User(String email, String encodedPw, String nickname, Provider provider, Alarm alarm, Action action) {
        this.email = email;
        this.password = encodedPw;
        this.nickname = nickname;
        this.provider = provider;
        this.alarm = alarm;
        this.action = action;
        this.roles = Collections.singletonList("ROLE_USER");
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateBeMyMessage(String beMyMessage) {
        this.beMyMessage = beMyMessage;
    }

    public void updatePassword(String password) {
        this.password = password;
    }

    public void updateprofileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateFcmTokenList(List<String> fcmTokenList) {
        this.fcmTokenList = fcmTokenList;
    }

    /*
    UserDetails Method
    */
    @ElementCollection(fetch = LAZY)
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
