package great.job.mytamin.global.service;

import great.job.mytamin.domain.myday.entity.Myday;
import great.job.mytamin.domain.myday.repository.MydayRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AdminService {

    private final MydayRepository mydayRepository;
    //private final NotificationService notificationService;

    /*
    마이데이 초기 데이터 생성
    */
    @Transactional
    public void initMyday() {
        LocalDateTime dateOfMyday = LocalDateTime.of(2022, 11, 21, 10, 0); // TODO : 알림 시간 설정
        Myday myday = new Myday(
                dateOfMyday,
                dateOfMyday, // 당일
                dateOfMyday.minusDays(1), // 하루 전
                dateOfMyday.minusDays(7) // 일주일 전
        );
        mydayRepository.save(myday);
    }

//    @Transactional(readOnly = true)
//    public void getMytaminPushList() throws IOException {
//        notificationService.notifyMytamin(LocalDateTime.now());
//    }
//
//    @Transactional(readOnly = true)
//    public void getMydayPushList() throws IOException {
//        notificationService.notifyMyday(LocalDateTime.now());
//    }

}
