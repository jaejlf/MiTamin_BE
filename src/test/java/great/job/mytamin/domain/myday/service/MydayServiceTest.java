package great.job.mytamin.domain.myday.service;

import great.job.mytamin.domain.myday.dto.response.MydayResponse;
import great.job.mytamin.domain.util.MydayUtil;
import great.job.mytamin.domain.util.TimeUtil;
import great.job.mytamin.global.support.CommonServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("Myday 서비스")
class MydayServiceTest extends CommonServiceTest {

    @Autowired
    private MydayService mydayService;

    @MockBean
    private TimeUtil timeUtil;

    @MockBean
    private MydayUtil mydayUtil;

    @DisplayName("이번 달의 마이데이")
    @Test
    void getMyday() {
        //given
        given(timeUtil.isCurrentMonth(any())).willReturn(false);
        given(mydayUtil.randomizeDateOfMyday()).willReturn(LocalDateTime.now());

        //when
        MydayResponse result = mydayService.getMyday(user);

        //then
        String expected = LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM월"));
        assertThat(result.getMyDayMMDD()).contains(expected);
    }

}