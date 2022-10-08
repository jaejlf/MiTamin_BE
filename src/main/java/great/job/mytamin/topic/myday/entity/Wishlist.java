package great.job.mytamin.topic.myday.entity;

import great.job.mytamin.topic.user.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import javax.persistence.*;

import static javax.persistence.FetchType.LAZY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Wishlist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishId;

    @Length(min = 1, max = 100)
    private String title;

    private int count;
    private boolean isHidden;
    private int order;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

}
