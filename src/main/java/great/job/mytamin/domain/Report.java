package great.job.mytamin.domain;

import great.job.mytamin.enumerate.MentalCondition;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MentalCondition mentalCondition;

    @Column(length = 100, nullable = false)
    private String feelingTag;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String todayReport;

    @OneToOne(mappedBy="report")
    private Mytamin mytamin;

}
