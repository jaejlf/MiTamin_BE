package great.job.mytamin.domain;

import great.job.mytamin.enumerate.CareCategory;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Care {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long careId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CareCategory careCategory;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String careMessage1;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String careMessage2;

}
