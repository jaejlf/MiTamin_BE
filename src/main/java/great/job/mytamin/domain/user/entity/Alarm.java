package great.job.mytamin.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Alarm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long alarmId;

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

    public Alarm(String mytaminHour, String mytaminMin, Boolean mytaminAlarmOn) {
        this.mytaminHour = mytaminHour;
        this.mytaminMin = mytaminMin;
        this.mytaminAlarmOn = mytaminAlarmOn;
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

}
