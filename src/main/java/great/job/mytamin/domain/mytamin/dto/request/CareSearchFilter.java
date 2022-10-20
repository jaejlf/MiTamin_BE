package great.job.mytamin.domain.mytamin.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CareSearchFilter {
    Integer sort;
    List<Integer> careCategoryCodeList;
    String date;
}
