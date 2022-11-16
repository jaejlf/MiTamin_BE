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

    public Myday(LocalDateTime dateOfMyday) {
        this.dateOfMyday = dateOfMyday;
    }

    /*
    마이데이 날짜 업데이트
    */
    public void updateDateOfMyday(LocalDateTime dateOfMyday) {
        this.dateOfMyday = dateOfMyday;
    }

}
