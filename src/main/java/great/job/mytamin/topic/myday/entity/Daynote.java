package great.job.mytamin.topic.myday.entity;

import great.job.mytamin.topic.user.entity.User;
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
    private String wishText; // 연관관계 삭제되었을 경우 -> raw text 활용

    @Column(columnDefinition = "TEXT", nullable = false)
    private String note;

    private LocalDateTime rawPerformedAt;
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    public Daynote(List<String> imgUrlList, Wish wish, String note, LocalDateTime rawPerformedAt, User user) {
        this.imgUrlList = imgUrlList;
        this.wish = wish;
        this.wishText = wish.getWishText();
        this.note = note;
        this.rawPerformedAt = rawPerformedAt;
        this.user = user;
    }

    public void updateAll(List<String> imgUrlList, Wish wish, String note) {
        this.imgUrlList = imgUrlList;
        this.wish = wish;
        this.wishText = wish.getWishText();
        this.note = note;
    }

    public void updateWish(Wish wish) {
        this.wishText = wish.getWishText();
    }

}