package great.job.mytamin.domain.daynote.entity;

import great.job.mytamin.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Daynote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long daynoteId;

    @ElementCollection
    @OrderColumn
    private List<String> imgList = new ArrayList<>();

    @Length(min = 1, max = 100)
    private String wishlistTitle; // 연관관계 설정 X -> raw text만 활용

    @Column(columnDefinition = "TEXT", nullable = false)
    private String comment;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

}