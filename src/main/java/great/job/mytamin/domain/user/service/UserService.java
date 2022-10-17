package great.job.mytamin.domain.user.service;

import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.service.AwsS3Service;
import great.job.mytamin.domain.user.dto.request.BeMyMsgRequest;
import great.job.mytamin.domain.user.dto.response.ProfileResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.repository.UserRepository;
import great.job.mytamin.domain.util.UserUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

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
    프로필 이미지 수정
    */
    public void updateProfileImg(User user, MultipartFile file) {
        awsS3Service.deleteImg(user.getProfileImgUrl()); // 기존 이미지 삭제
        user.updateprofileImgUrl(
                awsS3Service.uploadImg(file, user.getNickname())
        );
        userRepository.save(user);
    }

    /*
    닉네임 수정
    */
    @Transactional
    public void updateNickname(User user, String nickname) {
        if (userUtil.isNicknameDuplicate(nickname)) throw new MytaminException(NICKNAME_DUPLICATE_ERROR);
        user.updateNickname(nickname);
    }

    /*
    '되고 싶은 나' 메세지 수정
    */
    @Transactional
    public void updateBeMyMessage(User user, BeMyMsgRequest beMyMsgRequest) {
        String beMyMessage = beMyMsgRequest.getBeMyMessage();
        user.updateBeMyMessage(beMyMessage);
    }

}
