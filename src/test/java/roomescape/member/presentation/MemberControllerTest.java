package roomescape.member.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.common.ControllerTest;
import roomescape.member.application.MemberService;
import roomescape.member.dto.request.MemberJoinRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.TestFixture.*;

@WebMvcTest(MemberController.class)
class MemberControllerTest extends ControllerTest {
    @MockBean
    MemberService memberService;

    @Test
    @DisplayName("사용자 가입 POST 요청 시 상태코드 201을 반환한다.")
    void join() throws Exception {
        // given
        MemberJoinRequest request = new MemberJoinRequest(MIA_EMAIL, TEST_PASSWORD, MIA_NAME);

        BDDMockito.given(memberService.create(any()))
                .willReturn(USER_MIA());

        // when & then
        mockMvc.perform(post("/members/join")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isCreated());
    }
}
