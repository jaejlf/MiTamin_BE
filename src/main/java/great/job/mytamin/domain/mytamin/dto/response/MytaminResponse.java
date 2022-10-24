package great.job.mytamin.domain.mytamin.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.fasterxml.jackson.annotation.JsonInclude.*;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MytaminResponse {

    @JsonInclude(Include.NON_NULL)
    Long mytaminId;

    String takeAt;
    ReportResponse report;
    CareResponse care;

    public static MytaminResponse of(Mytamin mytamin,
                                     ReportResponse reportResponse,
                                     CareResponse careResponse) {
        LocalDateTime target = mytamin.getTakeAt();
        String dayOfWeek = target.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        return MytaminResponse.builder()
                .takeAt(target.format(DateTimeFormatter.ofPattern("MM.dd")) + "." + dayOfWeek)
                .report(reportResponse)
                .care(careResponse)
                .build();
    }

    public static MytaminResponse withId(Mytamin mytamin,
                                     ReportResponse reportResponse,
                                     CareResponse careResponse) {
        LocalDateTime target = mytamin.getTakeAt();
        String dayOfWeek = target.getDayOfWeek().getDisplayName(TextStyle.SHORT, Locale.US);
        return MytaminResponse.builder()
                .mytaminId(mytamin.getMytaminId())
                .takeAt(target.format(DateTimeFormatter.ofPattern("MM.dd")) + "." + dayOfWeek)
                .report(reportResponse)
                .care(careResponse)
                .build();
    }

}