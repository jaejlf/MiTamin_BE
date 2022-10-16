package great.job.mytamin.topic.myday.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UpdateWishRequest {
    List<WishRequest> wishRequest;
    List<Long> deletedIdList;
}
