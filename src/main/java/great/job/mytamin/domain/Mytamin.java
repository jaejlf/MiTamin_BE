package great.job.mytamin.domain;

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
public class Mytamin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mytaminId;

    private LocalDateTime ateMytaminAt;
    private LocalDateTime createdAt;
    
}
