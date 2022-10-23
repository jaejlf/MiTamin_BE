package great.job.mytamin.domain.myday.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
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

    @JsonIgnore
    int year;

    Long daynoteId;
    String performedAt;
    List<String> imgList;
    String month;
    String wishText;
    String note;

    public static DaynoteResponse of(Daynote daynote) {
        LocalDateTime rawPerformedAt = daynote.getRawPerformedAt();
        return DaynoteResponse.builder()
                .year(rawPerformedAt.getYear())
                .daynoteId(daynote.getDaynoteId())
                .performedAt(rawPerformedAt.getYear() + "년 " + rawPerformedAt.getMonth().getValue() + "월의 마이데이")
                .imgList(daynote.getImgUrlList())
                .month(rawPerformedAt.getMonth().getValue() + "월")
                .wishText(daynote.getWishText())
                .note(daynote.getNote())
                .build();
    }

}