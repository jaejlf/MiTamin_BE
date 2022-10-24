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
    @Column(nullable = false, unique = true)
    private String nickname;

    @Length(max = 300)
    private String profileImgUrl;

    @Length(min = 1, max = 20)
    private String beMyMessage = "마음 면역력이 높아질";

    private String refreshToken = "";
    private LocalDateTime createdAt = LocalDateTime.now();

    @Enumerated(EnumType.STRING)
    private Provider provider;

    /*
    행동 1 : 숨 고르기, 행동 2 : 감각 깨우기 실천 시간
    */
    private LocalDateTime breathTime = LocalDateTime.of(1999, 1, 1, 0, 0);
    private LocalDateTime senseTime = LocalDateTime.of(1999, 1, 1, 0, 0);

    /*
    이번 달의 마이 데이
    */
    private LocalDateTime dateOfMyday;

    /*
    마이타민 섭취 지정 시간
    */
    private String mytaminHour;
    private String mytaminMin;

    /*
    마이데이 알림 지정 시간
    */
    private String mydayWhen;

    /*
    알림 설정
    */
    private Boolean mytaminAlarmOn;
    private Boolean mydayAlarmOn = false;

    @OneToMany(mappedBy = "user", orphanRemoval = true)
    @JsonIgnore
    private Set<Mytamin> mytaminSet = new HashSet<>();

    public User(String email, String encodedPw, String nickname, Provider provider, String mytaminHour, String mytaminMin, Boolean mytaminAlarmOn) {
        this.email = email;
        this.password = encodedPw;
        this.nickname = nickname;
        this.provider = provider;
        this.mytaminHour = mytaminHour;
        this.mytaminMin = mytaminMin;
        this.mytaminAlarmOn = mytaminAlarmOn;
        this.roles = Collections.singletonList("ROLE_USER");
    }

    public void updateBreathTime() {
        this.breathTime = LocalDateTime.now();
    }

    public void updateSenseTime() {
        this.senseTime = LocalDateTime.now();
    }

    public void updateNickname(String nickname) {
        this.nickname = nickname;
    }

    public void updateBeMyMessage(String beMyMessage) {
        this.beMyMessage = beMyMessage;
    }

    /*
    마이타민 알림 설정
    */
    public void updateMytaminWhen(String mytaminHour, String mytaminMin) {
        this.mytaminHour = mytaminHour;
        this.mytaminMin = mytaminMin;
    }

    public void updateMytaminAlarmOn(Boolean isOn) {
        this.mytaminAlarmOn = isOn;
    }

    /*
    마이데이 알림 설정
    */
    public void updateMydayWhen(String mydayWhen) {
        this.mydayWhen = mydayWhen;
    }

    public void updateMydayAlarmOn(Boolean isOn) {
        this.mydayAlarmOn = isOn;
    }

    public void updateprofileImgUrl(String profileImgUrl) {
        this.profileImgUrl = profileImgUrl;
    }

    public void updateRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public void updateDateOfMyday(LocalDateTime dateOfMyday) {
        this.dateOfMyday = dateOfMyday;
    }

    /*
    기록 초기화
    */
    public void initData() {
        this.breathTime = LocalDateTime.of(1999, 1, 1, 0, 0);
        this.senseTime = LocalDateTime.of(1999, 1, 1, 0, 0);
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
