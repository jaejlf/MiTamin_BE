package great.job.mytamin.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.time.LocalDateTime;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Action {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long actionId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "user")
    private User user;

    private LocalDateTime breathTime; // 숨 고르기
    private LocalDateTime senseTime;  // 감각 깨우기

}
