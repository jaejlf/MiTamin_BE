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
@DisplayName("Alarm 컨트롤러")
class AlarmControllerTest extends CommonControllerTest {

    String docId = "/alarm/";

    @MockBean
    private AlarmService alarmService;

    @DisplayName("알림 설정 상태 조회")
    @Test
    void getAlarmSettingStatus(TestInfo testInfo) throws Exception {
        //given
        SettingInfoResponse mytamin = SettingInfoResponse.builder().isOn(true).when("오후 10:00").build();
        SettingInfoResponse myday = SettingInfoResponse.builder().isOn(true).when("당일").build();
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
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지"),
                                fieldWithPath("data.mytamin").description("마이타민 알림"),
                                fieldWithPath("data.myday").description("마이데이 알림"),
                                fieldWithPath("data.*.isOn").description("알림이 켜져있는지"),
                                fieldWithPath("data.*.when").description("알림 지정 시간")
                        ))
                );
    }
    
    @Nested
    @DisplayName("마이타민 알림 ON")
    class TurnOnMytaminAlarmTest {

        MytaminAlarmRequest mytaminAlarmRequest = new MytaminAlarmRequest("22", "00", "{{FCM_TOKEN}}");

        @DisplayName("성공")
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
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            requestFields(
                                    fieldWithPath("mytaminHour").description("*마이타민 섭취 지정 시간 HH (24시간)"),
                                    fieldWithPath("mytaminMin").description("*마이타민 섭취 지정 시간 MM"),
                                    fieldWithPath("fcmToken").description("*FCM 토큰")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지")
                            ))
                    );
        }
        
    }

    @DisplayName("마이타민 알림 OFF")
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
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지")
                        ))
                );
    }

    @Nested
    @DisplayName("마이데이 알림 ON")
    class TurnOnMydayAlarmTest {

        int code = 1;

        @DisplayName("성공")
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
                                    headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                            ),
                            pathParameters(
                                    parameterWithName("code").description("*마이데이 알림 지정 시간 코드")
                            ),
                            responseFields(
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("message").description("결과 메세지")
                            ))
                    );
        }

        @DisplayName("알림 지정 시간 코드 오류")
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
                                    fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                    fieldWithPath("errorCode").description("고유 에러 코드"),
                                    fieldWithPath("errorName").description("오류 이름"),
                                    fieldWithPath("message").description("오류 메세지")
                            ))
                    );
        }

    }

    @DisplayName("마이데이 알림 OFF")
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
                                headerWithName("X-AUTH-TOKEN").description("*액세스 토큰")
                        ),
                        responseFields(
                                fieldWithPath("statusCode").description("HTTP 상태 코드"),
                                fieldWithPath("message").description("결과 메세지")
                        ))
                );
    }

}