package great.job.mytamin.global.util;

import great.job.mytamin.domain.report.entity.Report;
import org.springframework.stereotype.Component;

@Component
public class ReportUtil {

    public String concatFeelingTag(Report report) {
        String feelingTag = "#" + report.getTag1();
        if (report.getTag2() != null) {
            feelingTag += " #" + report.getTag2();
        }
        if (report.getTag3() != null) {
            feelingTag += " #" + report.getTag3();
        }
        return feelingTag;
    }

}
