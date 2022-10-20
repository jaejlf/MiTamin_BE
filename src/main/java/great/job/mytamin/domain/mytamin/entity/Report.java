package great.job.mytamin.domain.mytamin.entity;

import great.job.mytamin.domain.user.entity.User;
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
public class Report {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long reportId;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    private int mentalConditionCode;

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
    private LocalDateTime takeAt;

    public Report(User user, int mentalConditionCode, String tag1, String tag2, String tag3, String todayReport, Mytamin mytamin) {
        this.user = user;
        this.mentalConditionCode = mentalConditionCode;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.todayReport = todayReport;
        this.mytamin = mytamin;
        this.takeAt = mytamin.getTakeAt();
    }

    public void updateAll(int mentalConditionCode, String tag1, String tag2, String tag3, String todayReport) {
        this.mentalConditionCode = mentalConditionCode;
        this.tag1 = tag1;
        this.tag2 = tag2;
        this.tag3 = tag3;
        this.todayReport = todayReport;
    }


}
