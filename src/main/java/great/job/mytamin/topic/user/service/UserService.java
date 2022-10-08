package great.job.mytamin.topic.user.service;

import great.job.mytamin.topic.user.dto.response.ProfileResponse;
import great.job.mytamin.topic.user.dto.request.BeMyMsgRequest;
import great.job.mytamin.topic.user.entity.User;
import great.job.mytamin.global.util.UserUtil;
import great.job.mytamin.global.exception.MytaminException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static great.job.mytamin.global.exception.ErrorMap.NICKNAME_DUPLICATE_ERROR;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserUtil userUtil;

    /*
    프로필 조회
    */
    @Transactional(readOnly = true)
    public ProfileResponse getProfile(User user) {
        return ProfileResponse.of(user);
    }

    /*
    닉네임 수정
    */
    @Transactional
    public void updateNickname(User user, String nickname) {
        if (userUtil.checkNicknameDuplication(nickname)) throw new MytaminException(NICKNAME_DUPLICATE_ERROR);
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
