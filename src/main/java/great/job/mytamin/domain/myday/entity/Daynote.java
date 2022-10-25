package great.job.mytamin.domain.myday.entity;

import great.job.mytamin.domain.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
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
    private List<String> imgUrlList = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "wishId")
    private Wish wish;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String note;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime performedAt;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    public Daynote(List<String> imgUrlList, Wish wish, String note, LocalDateTime performedAt, User user) {
        this.imgUrlList = imgUrlList;
        this.wish = wish;
        this.note = note;
        this.performedAt = performedAt;
        this.user = user;
    }

    public void updateAll(List<String> imgUrlList, Wish wish, String note) {
        this.imgUrlList = imgUrlList;
        this.wish = wish;
        this.note = note;
    }

}