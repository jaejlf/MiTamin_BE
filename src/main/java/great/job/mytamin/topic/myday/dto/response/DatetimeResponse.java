package great.job.mytamin.topic.myday.dto.response;

import great.job.mytamin.topic.myday.entity.Daynote;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DatetimeResponse {

    String year;
    String month;
    String str;

    public static DatetimeResponse of(Daynote daynote) {
        LocalDateTime rawPerformedAt = daynote.getRawPerformedAt();
        return DatetimeResponse.builder()
                .year(rawPerformedAt.format(DateTimeFormatter.ofPattern("yyyy")))
                .month(rawPerformedAt.format(DateTimeFormatter.ofPattern("MM")))
                .str(rawPerformedAt.format(DateTimeFormatter.ofPattern("yy년 MM월의 마이데이")))
                .build();
    }
}
