package great.job.mytamin.domain.myday.entity;

import great.job.mytamin.domain.user.entity.User;
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
public class Wish {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long wishId;

    @Length(min = 1, max = 100)
    private String wishText;

    private Boolean isHidden;
    private int orderId;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "userId")
    private User user;

    public Wish(String wishText, Boolean isHidden, int orderId, User user) {
        this.wishText = wishText;
        this.isHidden = isHidden;
        this.orderId = orderId;
        this.user = user;
    }

    public void updateWish(String text, Boolean isHidden, int orderId) {
        this.wishText = text;
        this.isHidden = isHidden;
        this.orderId = orderId;
    }

}
