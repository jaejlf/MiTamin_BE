package great.job.mytamin.domain.myday.dto.response;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import great.job.mytamin.domain.myday.entity.Daynote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String month;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String wishText;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    String note;

    public static DaynoteResponse ofDetail(Daynote daynote) {
        LocalDateTime rawPerformedAt = daynote.getRawPerformedAt();
        return DaynoteResponse.builder()
                .year(rawPerformedAt.getYear())
                .daynoteId(daynote.getDaynoteId())
                .performedAt(rawPerformedAt.format(DateTimeFormatter.ofPattern("yy년 MM월의 마이데이")))
                .imgList(daynote.getImgUrlList())
                .wishText(daynote.getWishText())
                .note(daynote.getNote())
                .build();
    }

    public static DaynoteResponse ofOverview(Daynote daynote) {
        LocalDateTime rawPerformedAt = daynote.getRawPerformedAt();
        return DaynoteResponse.builder()
                .year(rawPerformedAt.getYear())
                .daynoteId(daynote.getDaynoteId())
                .month(rawPerformedAt.format(DateTimeFormatter.ofPattern("MM")))
                .performedAt(rawPerformedAt.format(DateTimeFormatter.ofPattern("yy년 MM월의 마이데이")))
                .imgList(daynote.getImgUrlList())
                .build();
    }

}