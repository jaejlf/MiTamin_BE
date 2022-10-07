package great.job.mytamin.domain.user.service;

import great.job.mytamin.domain.user.dto.request.BeMyMsgRequest;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.util.UserUtil;
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
