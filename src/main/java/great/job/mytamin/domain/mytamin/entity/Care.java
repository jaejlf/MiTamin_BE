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
public class Care {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long careId;

    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    @Column(nullable = false)
    private String careCategory;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String careMsg1;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String careMsg2;

    @OneToOne(mappedBy = "care")
    private Mytamin mytamin;

    public Care(User user, String careCategory, String careMsg1, String careMsg2, Mytamin mytamin) {
        this.user = user;
        this.careCategory = careCategory;
        this.careMsg1 = careMsg1;
        this.careMsg2 = careMsg2;
        this.mytamin = mytamin;
    }

    public void updateAll(String careCategory, String careMsg1, String careMsg2) {
        this.careCategory = careCategory;
        this.careMsg1 = careMsg1;
        this.careMsg2 = careMsg2;
    }

}
