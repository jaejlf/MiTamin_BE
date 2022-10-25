package great.job.mytamin.domain.myday.dto.response;

import great.job.mytamin.domain.myday.entity.Daynote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DaynoteResponse {

    Long daynoteId;
    List<String> imgList;
    int year;
    int month;
    String wishText;
    String note;

    public static DaynoteResponse of(Daynote daynote) {
        LocalDateTime rawPerformedAt = daynote.getPerformedAt();
        return DaynoteResponse.builder()
                .daynoteId(daynote.getDaynoteId())
                .imgList(daynote.getImgUrlList())
                .year(rawPerformedAt.getYear())
                .month(rawPerformedAt.getMonth().getValue())
                .wishText(daynote.getWish().getWishText())
                .note(daynote.getNote())
                .build();
    }

}