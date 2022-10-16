package great.job.mytamin.topic.myday.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import great.job.mytamin.topic.myday.entity.Daynote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DaynoteResponse {

    List<String> imgList;
    String wishText;
    String note;
    DatetimeResponse performedAt;

    @JsonIgnore
    int year;

    public static DaynoteResponse of(Daynote daynote) {
        return DaynoteResponse.builder()
                .imgList(daynote.getImgList())
                .wishText(daynote.getWishText())
                .note(daynote.getNote())
                .performedAt(DatetimeResponse.of(daynote))
                .year(daynote.getRawPerformedAt().getYear())
                .build();
    }

}