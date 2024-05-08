package roomescape.auth.presentation;

import jakarta.servlet.http.Cookie;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import roomescape.auth.application.JwtTokenProvider;
import roomescape.auth.dto.request.LoginRequest;
import roomescape.common.ControllerTest;
import roomescape.member.application.MemberService;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static roomescape.TestFixture.*;

@WebMvcTest(AuthController.class)
class AuthControllerTest extends ControllerTest {
    @MockBean
    JwtTokenProvider jwtTokenProvider;

    @MockBean
    MemberService memberService;

    @Test
    @DisplayName("로그인 요청 시 상태코드 200을 반환한다.")
    void login() throws Exception {
        // given
        LoginRequest request = new LoginRequest(MIA_EMAIL, TEST_PASSWORD);

        BDDMockito.given(memberService.findByEmail(any()))
                .willReturn(USER_MIA());
        BDDMockito.given(jwtTokenProvider.createToken(any()))
                .willReturn("token");

        // when & then
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(header().string(HttpHeaders.SET_COOKIE, "token=token"));
    }

    @Test
    @DisplayName("인증 정보 GET 요청 시 상태코드 200을 반환한다.")
    void checkAuthInformation() throws Exception {
        // given
        Cookie cookie = new Cookie("token", "token");

        BDDMockito.given(memberService.findByEmail(any()))
                .willReturn(USER_MIA());

        // when
        mockMvc.perform(get("/login/check")
                        .cookie(cookie))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(MIA_NAME));
    }
}