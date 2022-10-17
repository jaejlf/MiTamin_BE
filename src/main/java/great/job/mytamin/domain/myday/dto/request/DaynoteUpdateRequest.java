package great.job.mytamin.domain.myday.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DaynoteUpdateRequest {
    List<MultipartFile> fileList;
    @NotBlank String wishText;
    @NotBlank String note;
}