package great.job.mytamin.domain.user.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotBlank;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfileUpdateRequest {
    MultipartFile file;
    @NotBlank Boolean isImgEdited;
    @NotBlank String nickname;
    @NotBlank String beMyMessage;
}
