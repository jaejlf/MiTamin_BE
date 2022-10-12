package great.job.mytamin.topic.myday.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WishlistResponse {

    List<WishResponse> published;
    List<WishResponse> hidden;

    public static WishlistResponse of(List<WishResponse> published, List<WishResponse> hidden) {
        return WishlistResponse.builder()
                .published(published)
                .hidden(hidden)
                .build();
    }

}
