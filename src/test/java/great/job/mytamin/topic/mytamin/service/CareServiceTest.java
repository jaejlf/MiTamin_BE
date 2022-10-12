package great.job.mytamin.topic.mytamin.service;

import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonServiceTest;
import great.job.mytamin.global.util.TimeUtil;
import great.job.mytamin.topic.mytamin.dto.request.CareRequest;
import great.job.mytamin.topic.mytamin.dto.response.CareResponse;
import great.job.mytamin.topic.mytamin.entity.Care;
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
    private TimeUtil timeUtil;

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

            given(mytaminService.findMytaminOrNew(any())).willReturn(mytamin);
            given(timeUtil.canEditCare(any())).willReturn(true);

            //when
            CareResponse result = careService.createCare(user, careRequest);

            //then
            CareResponse expected = CareResponse.builder()
                    .careId(1L)
                    .canEdit(true)
                    .careCategory("이루어 낸 일")
                    .careMsg1("오늘 할 일을 전부 했어")
                    .careMsg2("성실히 노력하는 내 모습이 좋아")
                    .build();

            assertAll(
                    () -> assertThat(result.getCanEdit()).isEqualTo(expected.getCanEdit()),
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

            given(mytaminService.findMytaminOrNew(any())).willReturn(mytamin);
            given(timeUtil.canEditCare(any())).willReturn(true);
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