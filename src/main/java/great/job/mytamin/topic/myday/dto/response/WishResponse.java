package great.job.mytamin.topic.myday.dto.response;

import great.job.mytamin.topic.myday.entity.Wish;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishResponse {

    Long wishId;
    String text;
    int count;

    public static WishResponse of(Wish wish, int count) {
        return WishResponse.builder()
                .wishId(wish.getWishId())
                .text(wish.getText())
                .count(count)
                .build();
    }

}
