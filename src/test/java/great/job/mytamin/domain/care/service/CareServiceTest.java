package great.job.mytamin.domain.care.service;

import great.job.mytamin.domain.care.dto.request.CareRequest;
import great.job.mytamin.domain.care.dto.response.CareResponse;
import great.job.mytamin.domain.care.entity.Care;
import great.job.mytamin.domain.mytamin.service.MytaminService;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.service.TimeService;
import great.job.mytamin.global.support.CommonServiceTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@DisplayName("Care 서비스")
class CareServiceTest extends CommonServiceTest {

    @Autowired
    private CareService careService;

    @MockBean
    private TimeService timeService;

    @MockBean
    private MytaminService mytaminService;

    @Nested
    @DisplayName("칭찬 처방하기")
    class CareTodayTest {

        @DisplayName("성공")
        @Test
        void careToday() {
            //given
            CareRequest careRequest = new CareRequest(
                    1,
                    "오늘 할 일을 전부 했어",
                    "성실히 노력하는 내 모습이 좋아"
            );
            given(timeService.convertToTakeAt(any())).willReturn(mockTakeAtNow);
            given(mytaminService.getMytamin(any(), any())).willReturn(mytamin);

            //when
            CareResponse result = careService.careToday(user, careRequest);

            //then
            CareResponse expected = CareResponse.of(
                    new Care(
                            "이루어 낸 일",
                            "오늘 할 일을 전부 했어",
                            "성실히 노력하는 내 모습이 좋아",
                            mytamin
                    )
            );
            assertAll(
                    () -> assertThat(result.getCareCategory()).isEqualTo(expected.getCareCategory()),
                    () -> assertThat(result.getCareMsg1()).isEqualTo(expected.getCareMsg1()),
                    () -> assertThat(result.getCareMsg2()).isEqualTo(expected.getCareMsg2())
            );
        }

        @DisplayName("이미 칭찬 처방 완료")
        @Test
        void careToday_5001() {
            //given
            CareRequest careRequest = new CareRequest(
                    1,
                    "오늘 할 일을 전부 했어",
                    "성실히 노력하는 내 모습이 좋아"
            );
            given(timeService.convertToTakeAt(any())).willReturn(mockTakeAtNow);
            given(mytaminService.getMytamin(any(), any())).willReturn(mytamin);

            mytamin.updateCare(new Care(
                    "이루어 낸 일",
                    "오늘 할 일을 전부 했어",
                    "성실히 노력하는 내 모습이 좋아",
                    mytamin
            ));

            //when & then
            assertThatThrownBy(() -> careService.careToday(user, careRequest))
                    .isInstanceOf(MytaminException.class)
                    .hasMessageContaining("CARE_ALREADY_DONE");
        }

    }

}