package great.job.mytamin.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class FcmOn {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long fcmOnId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    private String fcmToken;

    // 마이타민 섭취 지정 시간 (HH : mm)
    private String mytaminWhen;

    public FcmOn(User user, String fcmToken) {
        this.user = user;
        this.fcmToken = fcmToken;
        this.mytaminWhen = user.getAlarm().getMytaminHour() + " : " + user.getAlarm().getMytaminMin();
    }

    public void updateMytaminWhen(User user) {
        this.mytaminWhen = user.getAlarm().getMytaminHour() + " : " + user.getAlarm().getMytaminMin();
    }

}
