package great.job.mytamin.domain.util;

import great.job.mytamin.domain.user.entity.User;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class FcmUtil {

    /*
    로그인 시,
    DB에 저장되어 있지 않은 FCM 토큰이라면 추가
    */
    public List<String> addFcmToken(User user, String fcmToken) {
        List<String> fcmTokenList = user.getFcmTokenList();
        if(!fcmTokenList.contains(fcmToken)) {
            fcmTokenList.add(fcmToken);
        }
        return fcmTokenList;
    }
    
    /*
    로그아웃 시,
    FCM 토큰 삭제
    */
    public void deleteFcmToken(User user, String fcmToken) {
        List<String> fcmTokenList = user.getFcmTokenList();
        fcmTokenList.remove(fcmToken);
        user.updateFcmTokenList(fcmTokenList);
    }

}
