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
    String wishText;
    int count;

    public static WishResponse of(Wish wish, int count) {
        return WishResponse.builder()
                .wishId(wish.getWishId())
                .wishText(wish.getWishText())
                .count(count)
                .build();
    }

}
