package great.job.mytamin.domain.user.service;

import great.job.mytamin.domain.myday.service.DaynoteService;
import great.job.mytamin.domain.myday.service.WishService;
import great.job.mytamin.domain.mytamin.service.CareService;
import great.job.mytamin.domain.mytamin.service.ReportService;
import great.job.mytamin.domain.user.dto.request.InitRequest;
import great.job.mytamin.domain.user.dto.request.ProfileUpdateRequest;
import great.job.mytamin.domain.user.dto.response.ProfileResponse;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.domain.user.repository.UserRepository;
import great.job.mytamin.domain.util.FcmUtil;
import great.job.mytamin.domain.util.UserUtil;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.service.AwsS3Service;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;
import java.util.Objects;

import static great.job.mytamin.global.exception.ErrorMap.NICKNAME_DUPLICATE_ERROR;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserUtil userUtil;
    private final FcmUtil fcmUtil;
    private final AwsS3Service awsS3Service;
    private final DaynoteService daynoteService;
    private final WishService wishService;
    private final ReportService reportService;
    private final CareService careService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

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
    public void updateProfile(User user, ProfileUpdateRequest request) {
        if (request.getIsImgEdited().equals("T")) updateProfileImg(user, request.getFile()); // "T"일 경우에만 이미지 업데이트
        updateNickname(user, request.getNickname());
        updateBeMyMessage(user, request.getBeMyMessage());
        userRepository.save(user);
    }

    /*
    로그아웃
    */
    @Transactional
    public void logout(User user, Map<String, String> request) {
        user.updateRefreshToken(""); // 리프레쉬 토큰 삭제
        fcmUtil.deleteFcmToken(user, request.get("fcmToekn")); // FCM 토큰 삭제
        userRepository.save(user);
    }

    /*
    기록 초기화
    */
    @Transactional
    public void initData(User user, InitRequest request) {
        if (request.isInitReport()) initReport(user);
        if (request.isInitCare()) initCare(user);
        if (request.isInitMyday()) initMyday(user);
    }

    /*
    회원 탈퇴
    */
    @Transactional
    public void withdraw(User user) {
        initData(user, new InitRequest(true, true, true)); // 기록 초기화
        awsS3Service.deleteImg(user.getProfileImgUrl()); // 프로필 이미지 삭제
        userRepository.delete(user); // 유저 삭제
    }

    /*
   비밀번호 변경
   */
    @Transactional
    public void changePassword(User user, String password) {
        userUtil.validatePasswordPattern(password);
        user.updatePassword(passwordEncoder.encode(password));
        userRepository.save(user);
    }

    private void updateProfileImg(User user, MultipartFile file) {
        awsS3Service.deleteImg(user.getProfileImgUrl()); // 기존 이미지 삭제
        user.updateprofileImgUrl(
                awsS3Service.uploadImg(file, "PF")
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

    private void initReport(User user) {
        user.getAction().initData(); // 숨 고르기, 감각 깨우기 데이터 초기화
        userRepository.save(user);
        reportService.deleteAll(user);
    }

    private void initCare(User user) {
        user.getAction().initData(); // 숨 고르기, 감각 깨우기 데이터 초기화
        userRepository.save(user);
        careService.deleteAll(user);
    }

    private void initMyday(User user) {
        daynoteService.deleteAll(user);
        wishService.deleteAll(user);
    }

}
