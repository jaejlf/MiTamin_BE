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
    private List<String> imgList = new ArrayList<>();

    @OneToOne
    @JoinColumn(name = "wishId")
    private Wish wish;
    private String rawWishText; // 연관관계 삭제되었을 경우 -> raw text 활용

    @Column(columnDefinition = "TEXT", nullable = false)
    private String note;

    private LocalDateTime performedAt; // 마이데이를 수행한 날짜
    private LocalDateTime createdAt = LocalDateTime.now();

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    public Daynote(List<String> imgList, Wish wish, String note, LocalDateTime performedAt) {
        this.imgList = imgList;
        this.wish = wish;
        this.rawWishText = wish.getText();
        this.note = note;
        this.performedAt = performedAt;
    }

    public void updateWish(Wish wish) {
        this.rawWishText = wish.getText();
    }

}