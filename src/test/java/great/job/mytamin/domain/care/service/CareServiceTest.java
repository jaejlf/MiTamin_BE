package great.job.mytamin.domain.care.service;

import great.job.mytamin.domain.care.dto.request.CareRequest;
import great.job.mytamin.domain.care.dto.response.CareResponse;
import great.job.mytamin.domain.care.entity.Care;
import great.job.mytamin.domain.mytamin.service.MytaminService;
import great.job.mytamin.global.exception.MytaminException;
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
    private MytaminService mytaminService;

    @Nested
    @DisplayName("칭찬 처방하기")
    class CreateCareTest {

        @DisplayName("성공")
        @Test
        void createCare() {
            //given
            CareRequest careRequest = new CareRequest(
                    1,
                    "오늘 할 일을 전부 했어",
                    "성실히 노력하는 내 모습이 좋아"
            );

            given(mytaminService.getMytaminOrNew(any())).willReturn(mytamin);

            //when
            CareResponse result = careService.createCare(user, careRequest);

            //then
            CareResponse expected = CareResponse.of(care);
            assertAll(
                    () -> assertThat(result.getCareCategory()).isEqualTo(expected.getCareCategory()),
                    () -> assertThat(result.getCareMsg1()).isEqualTo(expected.getCareMsg1()),
                    () -> assertThat(result.getCareMsg2()).isEqualTo(expected.getCareMsg2())
            );
        }

        @DisplayName("이미 칭찬 처방 완료")
        @Test
        void createCare_5001() {
            //given
            CareRequest careRequest = new CareRequest(
                    1,
                    "오늘 할 일을 전부 했어",
                    "성실히 노력하는 내 모습이 좋아"
            );

            given(mytaminService.getMytaminOrNew(any())).willReturn(mytamin);
            updateCare(); // CARE ALREADY DONE

            //when & then
            assertThatThrownBy(() -> careService.createCare(user, careRequest))
                    .isInstanceOf(MytaminException.class)
                    .hasMessageContaining("CARE_ALREADY_DONE");
        }

    }

    private void updateCare() {
        mytamin.updateCare(
                new Care(
                        "이루어 낸 일",
                        "오늘 할 일을 전부 했어",
                        "성실히 노력하는 내 모습이 좋아",
                        mytamin
                )
        );
    }

}