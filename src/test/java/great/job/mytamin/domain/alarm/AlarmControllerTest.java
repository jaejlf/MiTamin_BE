package great.job.mytamin.domain.alarm;

import great.job.mytamin.domain.alarm.controller.AlarmController;
import great.job.mytamin.domain.alarm.service.AlarmService;
import great.job.mytamin.domain.user.dto.request.MytaminAlarmRequest;
import great.job.mytamin.domain.user.dto.response.SettingInfoResponse;
import great.job.mytamin.domain.user.dto.response.SettingResponse;
import great.job.mytamin.global.exception.MytaminException;
import great.job.mytamin.global.support.CommonControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.ResultActions;

import static great.job.mytamin.global.exception.ErrorMap.INVALID_MYDAY_ALARM_CODE_ERROR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch;
import static org.springframework.restdocs.payload.PayloadDocumentation.*;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AlarmController.class)
@DisplayName("Alarm ????????????")
class AlarmControllerTest extends CommonControllerTest {

    String docId = "/alarm/";

    @MockBean
    private AlarmService alarmService;

    @DisplayName("?????? ?????? ?????? ??????")
    @Test
    void getAlarmSettingStatus(TestInfo testInfo) throws Exception {
        //given
        SettingInfoResponse mytamin = SettingInfoResponse.builder().isOn(true).when("?????? 10:00").build();
        SettingInfoResponse myday = SettingInfoResponse.builder().isOn(true).when("??????").build();
        given(alarmService.getAlarmSettingStatus(any())).willReturn(
                SettingResponse.builder()
                        .mytamin(mytamin)
                        .myday(myday)
                        .build()
        );

        //when
        ResultActions actions = mockMvc.perform(get("/alarm/status")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("data").exists())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????"),
                                fieldWithPath("data.mytamin").description("???????????? ??????"),
                                fieldWithPath("data.myday").description("???????????? ??????"),
                                fieldWithPath("data.*.isOn").description("????????? ???????????????"),
                                fieldWithPath("data.*.when").description("?????? ?????? ??????")
                        ))
                );
    }
    
    @Nested
    @DisplayName("???????????? ?????? ON")
    class TurnOnMytaminAlarmTest {

        MytaminAlarmRequest mytaminAlarmRequest = new MytaminAlarmRequest("22", "00", "{{FCM_TOKEN}}");

        @DisplayName("??????")
        @Test
        void turnOnMytaminAlarm(TestInfo testInfo) throws Exception {
            //given
            doNothing().when(alarmService).turnOnMytaminAlarm(any(), any());

            //when
            ResultActions actions = mockMvc.perform(patch("/alarm/mytamin/on")
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .content(objectMapper.writeValueAsString(mytaminAlarmRequest))
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            requestFields(
                                    fieldWithPath("mytaminHour").description("*???????????? ?????? ?????? ?????? HH (24??????)"),
                                    fieldWithPath("mytaminMin").description("*???????????? ?????? ?????? ?????? MM"),
                                    fieldWithPath("fcmToken").description("*FCM ??????")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }
        
    }

    @DisplayName("???????????? ?????? OFF")
    @Test
    void turnOffMytaminAlarm(TestInfo testInfo) throws Exception {
        //given
        doNothing().when(alarmService).turnOffMytaminAlarm(any());

        //when
        ResultActions actions = mockMvc.perform(patch("/alarm/mytamin/off")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????")
                        ))
                );
    }

    @Nested
    @DisplayName("???????????? ?????? ON")
    class TurnOnMydayAlarmTest {

        int code = 1;

        @DisplayName("??????")
        @Test
        void turnOnMydayAlarm(TestInfo testInfo) throws Exception {
            //given
            doNothing().when(alarmService).turnOnMydayAlarm(any(), anyInt());

            //when
            ResultActions actions = mockMvc.perform(patch("/alarm/myday/on/{code}", code)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            requestHeaders(
                                    headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                            ),
                            pathParameters(
                                    parameterWithName("code").description("*???????????? ?????? ?????? ?????? ??????")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

        @DisplayName("?????? ?????? ?????? ?????? ??????")
        @Test
        void turnOnMydayAlarm_3002(TestInfo testInfo) throws Exception {
            //given
            doThrow(new MytaminException(INVALID_MYDAY_ALARM_CODE_ERROR)).when(alarmService).turnOnMydayAlarm(any(), anyInt());

            //when
            ResultActions actions = mockMvc.perform(patch("/alarm/myday/on/{code}", code)
                    .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                    .contentType(APPLICATION_JSON));

            //then
            actions
                    .andDo(print())
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("errorCode").value(3002))
                    .andExpect(jsonPath("errorName").value("INVALID_MYDAY_ALARM_CODE_ERROR"))
                    .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                    fieldWithPath("errorCode").description("?????? ?????? ??????"),
                                    fieldWithPath("errorName").description("?????? ??????"),
                                    fieldWithPath("message").description("?????? ?????????")
                            ))
                    );
        }

    }

    @DisplayName("???????????? ?????? OFF")
    @Test
    void turnOffMydayAlarm(TestInfo testInfo) throws Exception {
        //given
        doNothing().when(alarmService).turnOffMydayAlarm(any());

        //when
        ResultActions actions = mockMvc.perform(patch("/alarm/myday/off")
                .header("X-AUTH-TOKEN", "{{ACCESS_TOKEN}}")
                .contentType(APPLICATION_JSON));

        //then
        actions
                .andDo(print())
                .andExpect(status().isOk())
                .andDo(document(docId + testInfo.getTestMethod().get().getName(),
                        requestHeaders(
                                headerWithName("X-AUTH-TOKEN").description("*????????? ??????")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP ?????? ??????"),
                                fieldWithPath("message").description("?????? ?????????")
                        ))
                );
    }

}