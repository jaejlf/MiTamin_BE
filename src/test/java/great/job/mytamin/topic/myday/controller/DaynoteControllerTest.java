package great.job.mytamin.topic.myday.controller;

import great.job.mytamin.global.support.CommonControllerTest;
import great.job.mytamin.topic.myday.service.DaynoteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;

@WebMvcTest(controllers = DaynoteController.class)
@DisplayName("Daynote 컨트롤러")
class DaynoteControllerTest extends CommonControllerTest {

    String docId = "/daynote/";

    @MockBean
    private DaynoteService daynoteService;

    @DisplayName("데이노트 작성 가능 여부")
    @Test
    void canCreateDaynote(TestInfo testInfo) throws Exception {

    }

    @Nested
    @DisplayName("데이노트 작성하기")
    class CreateDaynoteTest {

        @DisplayName("성공")
        @Test
        void createDaynote(TestInfo testInfo) throws Exception {

        }

        @DisplayName("이미 해당 년/월에 데이노트 존재")
        @Test
        void createDaynote_8003(TestInfo testInfo) throws Exception {

        }

    }

    @DisplayName("데이노트 리스트 조회")
    @Test
    void getDaynoteList(TestInfo testInfo) throws Exception {

    }

}