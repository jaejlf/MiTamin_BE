package great.job.mytamin.domain.user.service;

import great.job.mytamin.global.service.RedisService;
import great.job.mytamin.domain.user.dto.request.EmailCheckRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.time.Duration;
import java.util.Random;

import static javax.mail.Message.RecipientType.TO;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender emailSender;
    private final RedisService redisService;

    @Value("${spring.mail.username}")
    private String from;

    /*
    이메일 인증 코드 전송
    */
    public void sendAuthCode(String to) throws MessagingException {
        // 인증 번호 발급
        String authCode = createCode();

        // 이메일 전송
        MimeMessage message = emailSender.createMimeMessage();
        message.addRecipients(TO, to);
        message.setSubject("[마이타민] 이메일 인증번호가 발급되었습니다.");
        message.setFrom(from);
        message.setText(getContent(authCode), "utf-8", "html");
        emailSender.send(message);

        // redis DB에 저장
        redisService.setValues(to, authCode, Duration.ofMillis(600000));
    }

    /*
    이메일 인증 코드 확인
    */
    public Boolean confirmAuthCode(EmailCheckRequest emailCheckRequest) {
        String email = emailCheckRequest.getEmail();
        String authCode = emailCheckRequest.getAuthCode();
        return authCode.equals(redisService.getValues(email));
    }

    private String createCode() {
        StringBuilder key = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 8; i++) {
            int index = random.nextInt(3);

            switch (index) {
                case 0:
                    key.append((char) (random.nextInt(26) + 97));
                    break;
                case 1:
                    key.append((char) (random.nextInt(26) + 65));
                    break;
                case 2:
                    key.append((random.nextInt(10)));
                    break;
            }
        }
        return key.toString();
    }

    private String getContent(String authCode) {
        return "<p style=\"font-size:10pt;font-family:sans-serif;padding:0 0 0 10pt\"><br></p><table summary=\"회원 가입 인증 메일\" cellpadding=\"0\" cellspacing=\"0\" width=\"600\" border=\"0\" style=\"border-collapse: collapse;\">\n" +
                "    <tbody><tr><td width=\"40\" style=\"font-size: 0;line-height: 0;\"><p></p><p><br></p></td><td width=\"520\" style=\"font-size: 0;line-height: 0;\"><table cellpadding=\"0\" cellspacing=\"0\" width=\"100%\" border=\"0\" style=\"border-collapse: collapse;\"><tbody><tr><td width=\"520\" style=\"\"><p style=\"\"><br></p><p style=\"font-size: 0px;\"><span style=\"font-family: Nanum Gothic, Malgun Gothic, dotum, AppleGothic, Helvetica, Arial, sans-serif; font-size: 14pt; line-height: 1.3; letter-spacing: -1px; color: rgb(255, 79, 46);\"><b>마이타민 회원가입</b></span></p></td></tr><tr><td width=\"520\" height=\"10\" style=\"font-size: 0;line-height: 0;\"></td></tr><tr><td width=\"520\" style=\"font-size: 0;\"><span style=\"font-family: 'Nanum Gothic','Malgun Gothic', 'dotum','AppleGothic', Helvetica, Arial, Sans-Serif;font-size: 22px;line-height: 1.3;letter-spacing: -1px;\"><b>인증메일</b></span></td></tr><tr><td width=\"520\" height=\"25\" style=\"font-size: 0;line-height: 0;border-bottom: 2px solid #000;\"></td></tr><tr><td width=\"520\" height=\"50\" style=\"font-size: 0;line-height: 0;\"></td></tr><tr><td width=\"520\" style=\"font-size: 0;\"><span style=\"font-family: 'Nanum Gothic','Malgun Gothic', 'dotum','AppleGothic', Helvetica, Arial, Sans-Serif;font-size: 16px;line-height: 1.6;letter-spacing: -1px;\">마이타민 애플리케이션 회원가입을 위한<br>인증번호 발송 메일입니다.<br><br>아래의 인증번호를 사용하여 이메일 주소 인증을 완료해주세요.</span></td></tr><tr><td width=\"520\" height=\"50\" style=\"font-size: 0;line-height: 0;\"></td></tr><tr><td align=\"center\" width=\"600\" height=\"50\" colspan=\"3\" bgcolor=\"#efefef\" style=\"font-family: 'Nanum Gothic','Malgun Gothic', 'dotum','AppleGothic', Helvetica, Arial, Sans-Serif;font-size: 22px;line-height: 1.6;letter-spacing: -1px;font-weight:bold\">" + authCode + "</td></tr><tr><td width=\"520\" height=\"50\" style=\"font-size: 0;line-height: 0;\"></td></tr></tbody></table></td></tr></tbody></table>";
    }

}
