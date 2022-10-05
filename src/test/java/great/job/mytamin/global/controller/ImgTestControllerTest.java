package great.job.mytamin.global.controller;

import great.job.mytamin.global.service.AwsS3Service;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.ResultActions;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.MULTIPART_FORM_DATA;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.multipart;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;
import static org.springframework.restdocs.request.RequestDocumentation.requestParts;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ImgTestController.class)
@DisplayName("ImgTest 컨트롤러")
class ImgTestControllerTest extends CommonControllerTest {

    @MockBean
    private AwsS3Service awsS3Service;

    @DisplayName("단일 이미지 업로드")
    @Test
    void uploadImgOne(TestInfo testInfo) throws Exception {
        //given
        MockMultipartFile file = new MockMultipartFile("file", "mock1.jpg", "image/jpg", "<<image>>".getBytes());
        given(awsS3Service.uploadImg(any(), any())).willReturn("{{IMAGE_URL}}");

        //when
        ResultActions actions = mockMvc.perform(multipart("/img/one")
                .file(file)
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(MULTIPART_FORM_DATA));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/img/" + testInfo.getTestMethod().get().getName(),
                        requestParts(
                                partWithName("file").description("업로드할 이미지 (.png, .jpg, .jpeg)")
                        ),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data").description("업로드된 이미지 URL")
                        ))
                );
    }

    @DisplayName("이미지 리스트 업로드")
    @Test
    void uploadImgList(TestInfo testInfo) throws Exception {
        //given
        List<MockMultipartFile> fileList = List.of(
                new MockMultipartFile("file", "mock1.jpg", "image/jpg", "<<image>>".getBytes()),
                new MockMultipartFile("file", "mock2.jpg", "image/jpg", "<<image>>".getBytes())
        );
        List<String> uploadUrl = new ArrayList<>();
        uploadUrl.add("{{IMAGE_URL_1}}");
        uploadUrl.add("{{IMAGE_URL_2}}");
        given(awsS3Service.uploadImageList(any(), any())).willReturn(uploadUrl);

        //when
        ResultActions actions = mockMvc.perform(multipart("/img/list")
                .file("file", fileList.get(0).getBytes())
                .file("file", fileList.get(1).getBytes())
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(MULTIPART_FORM_DATA));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document("/img/" + testInfo.getTestMethod().get().getName(),
                        requestParts(
                                partWithName("file").description("업로드할 이미지 (.png, .jpg, .jpeg)")
                        ),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data").description("업로드된 이미지 URL 리스트")
                        ))
                );
    }

}