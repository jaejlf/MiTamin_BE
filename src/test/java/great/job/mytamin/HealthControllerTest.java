package great.job.mytamin;

import great.job.mytamin.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.ResultActions;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = HealthController.class)
@DisplayName("Health 컨트롤러")
class HealthControllerTest extends CommonControllerTest {

    @DisplayName("Docs 발행 테스트")
    @Test
    void helloDocs(TestInfo testInfo) throws Exception {
        //given & when
        ResultActions actions = mockMvc.perform(get("/health/hello")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document("/health/" + testInfo.getTestMethod().get().getName(),
                        responseFields(
                                fieldWithPath("hello").description("hello docs !")
                        ))
                );
    }

}