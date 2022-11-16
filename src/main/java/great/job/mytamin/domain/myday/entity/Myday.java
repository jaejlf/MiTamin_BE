package great.job.mytamin.domain.myday.entity;

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
public class Myday {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mydayId;

    /*
    이번 달의 마이 데이
    */
    private LocalDateTime dateOfMyday;
    private LocalDateTime today;
    private LocalDateTime dayAgo;
    private LocalDateTime weekAgo;

    public Myday(LocalDateTime dateOfMyday, LocalDateTime today, LocalDateTime dayAgo, LocalDateTime weekAgo) {
        this.dateOfMyday = dateOfMyday;
        this.today = today;
        this.dayAgo = dayAgo;
        this.weekAgo = weekAgo;
    }

    /*
    마이데이 날짜 업데이트
    */
    public void updateDateOfMyday(LocalDateTime dateOfMyday) {
        this.dateOfMyday = dateOfMyday;
    }

}
