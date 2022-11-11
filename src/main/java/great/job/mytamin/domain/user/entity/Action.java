package great.job.mytamin.domain.user.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actionId;

    // 숨 고르기
    private LocalDateTime breathTime = LocalDateTime.of(1999, 1, 1, 0, 0);

    // 감각 깨우기
    private LocalDateTime senseTime = LocalDateTime.of(1999, 1, 1, 0, 0);

    public void updateBreathTime() {
        this.breathTime = LocalDateTime.now();
    }

    public void updateSenseTime() {
        this.senseTime = LocalDateTime.now();
    }

    /*
    기록 초기화
    */
    public void initData() {
        this.breathTime = LocalDateTime.of(1999, 1, 1, 0, 0);
        this.senseTime = LocalDateTime.of(1999, 1, 1, 0, 0);
    }

}
