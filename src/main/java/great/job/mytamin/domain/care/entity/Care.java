package great.job.mytamin.domain.care.entity;

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
public class Care {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long careId;

    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(nullable = false)
    private String careCategory;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String careMsg1;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String careMsg2;

    @OneToOne(mappedBy = "report")
    private Mytamin mytamin;

    public Care(String careCategory, String careMsg1, String careMsg2, Mytamin mytamin) {
        this.careCategory = careCategory;
        this.careMsg1 = careMsg1;
        this.careMsg2 = careMsg2;
        this.mytamin = mytamin;
    }

}
