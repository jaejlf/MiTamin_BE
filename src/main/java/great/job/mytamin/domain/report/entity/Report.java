package great.job.mytamin.domain.report.entity;

import great.job.mytamin.domain.mytamin.entity.Mytamin;
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
    private String feelingTag;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String todayReport;

    @OneToOne(mappedBy = "report")
    private Mytamin mytamin;

    public Report(String mentalCondition, String feelingTag, String todayReport, Mytamin mytamin) {
        this.mentalCondition = mentalCondition;
        this.feelingTag = feelingTag;
        this.todayReport = todayReport;
        this.mytamin = mytamin;
    }

}
