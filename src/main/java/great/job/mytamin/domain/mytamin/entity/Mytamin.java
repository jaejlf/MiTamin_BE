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
public class Mytamin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long mytaminId;

    private LocalDateTime takeAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "reportId")
    private Report report;

    @OneToOne(orphanRemoval = true)
    @JoinColumn(name = "careId")
    private Care care;

    public Mytamin(LocalDateTime takeAt, User user) {
        this.takeAt = takeAt;
        this.user = user;
    }

    public void updateReport(Report report) {
        this.report = report;
    }

    public void updateCare(Care care) {
        this.care = care;
    }

}
