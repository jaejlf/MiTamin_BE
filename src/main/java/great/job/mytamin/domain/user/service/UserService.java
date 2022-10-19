package great.job.mytamin.domain.user.service;

import great.job.mytamin.domain.user.dto.request.ProfileUpdateRequest;
import great.job.mytamin.domain.user.dto.response.ProfileResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.repository.UserRepository;
import great.job.mytamin.domain.util.UserUtil;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

import static great.job.mytamin.global.exception.ErrorMap.NICKNAME_DUPLICATE_ERROR;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserUtil userUtil;
    private final AwsS3Service awsS3Service;
    private final UserRepository userRepository;

    /*
    프로필 조회
    */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(User user) {
        return ProfileResponse.of(user);
    }

    /*
    프로필 편집
    */
    @Transactional
    public void updateProfile(User user, ProfileUpdateRequest profileUpdateRequest) {
        if (profileUpdateRequest.getIsImgEdited()) updateProfileImg(user, profileUpdateRequest.getFile());
        updateNickname(user, profileUpdateRequest.getNickname());
        updateBeMyMessage(user, profileUpdateRequest.getBeMyMessage());

        userRepository.save(user);
    }

    private void updateProfileImg(User user, MultipartFile file) {
        awsS3Service.deleteImg(user.getProfileImgUrl()); // 기존 이미지 삭제
        user.updateprofileImgUrl(
                awsS3Service.uploadImg(file, user.getNickname())
        );
    }

    private void updateNickname(User user, String nickname) {
        if (Objects.equals(user.getNickname(), nickname)) return; // 변경되지 않았다면
        if (userUtil.isNicknameDuplicate(nickname)) throw new MytaminException(NICKNAME_DUPLICATE_ERROR);
        user.updateNickname(nickname);
    }

    private void updateBeMyMessage(User user, String beMyMessage) {
        user.updateBeMyMessage(beMyMessage);
    }

}
