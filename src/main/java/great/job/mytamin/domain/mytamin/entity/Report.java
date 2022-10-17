package great.job.mytamin.domain.mytamin.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private String mentalCondition;

    @Column(length = 100, nullable = false)
    private String tag1;

    @Column(length = 100)
    private String tag2;

    @Column(length = 100)
    private String tag3;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String todayReport;

    @OneToOne(mappedBy = "report")
    private Mytamin mytamin;

    public Report(String mentalCondition, String tag1, String tag2, String tag3, String todayReport, Mytamin mytamin) {
        this.mentalCondition = mentalCondition;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.todayReport = todayReport;
        this.mytamin = mytamin;
    }

    public void updateAll(String mentalCondition, String tag1, String tag2, String tag3, String todayReport) {
        this.mentalCondition = mentalCondition;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.todayReport = todayReport;
    }


}
