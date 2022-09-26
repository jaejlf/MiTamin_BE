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
public class Mytamin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mytaminId;

    private LocalDateTime ateMytaminAt;
    private LocalDateTime createdAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @OneToOne
    @JoinColumn(name = "reportId")
    private Report report;

    @OneToOne
    @JoinColumn(name = "careId")
    private Care care;
    
}
