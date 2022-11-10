package great.job.mytamin.global.support;

import com.fasterxml.jackson.databind.ObjectMapper;
import great.job.mytamin.domain.mytamin.entity.Mytamin;
import great.job.mytamin.domain.user.entity.Action;
import great.job.mytamin.domain.user.entity.Alarm;
import great.job.mytamin.domain.user.entity.User;
import great.job.mytamin.global.jwt.JwtAuthInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;

import java.time.LocalDateTime;

import static great.job.mytamin.domain.user.enumerate.Provider.DEFAULT;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.documentationConfiguration;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.modifyUris;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;

@ExtendWith(RestDocumentationExtension.class)
@AutoConfigureRestDocs(uriHost = "MY-TAMIN-URL", uriPort = 80)
public class CommonControllerTest {

    public MockMvc mockMvc;
    public ObjectMapper objectMapper = new ObjectMapper();

    @MockBean
    public JwtAuthInterceptor jwtAuthInterceptor;

    // Mock Data
    public User user;
    public Mytamin mytamin;

    @BeforeEach
    public void setUp(WebApplicationContext wac, RestDocumentationContextProvider restDoc) {

        // Mock User
        user = new User(
                "mytamin@naver.com",
                "{{ENCODED_PASSWORD}}",
                "강철멘탈",
                DEFAULT,
                new Alarm("22", "00", true),
                new Action()
        );

        // Mock Mytamin
        LocalDateTime rawTakeAt = LocalDateTime.now();
        mytamin = new Mytamin(
                convertToMytaminDate(rawTakeAt),
                user
        );

        //인터셉터 통과
        given(jwtAuthInterceptor.preHandle(any(), any(), any())).willReturn(true);

        //로그인 정보 세팅
        Authentication authentication = new UsernamePasswordAuthenticationToken(user, "", user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        this.mockMvc = MockMvcBuilders
                .webAppContextSetup(wac)
                .addFilters(new CharacterEncodingFilter("UTF-8", true))
                .apply(documentationConfiguration(restDoc)
                        .operationPreprocessors()
                        .withRequestDefaults(modifyUris().host("MY-TAMIN-URL").removePort(), prettyPrint())
                        .withResponseDefaults(prettyPrint())
                )
                .build();
    }

    private LocalDateTime convertToMytaminDate(LocalDateTime target) {
        if (target.getHour() <= 4) target = target.minusDays(1);
        return LocalDateTime.of(target.getYear(), target.getMonth().getValue(), target.getDayOfMonth(), 10, 0);
    }


}
