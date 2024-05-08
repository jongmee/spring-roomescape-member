package roomescape.reservation.presentation;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import roomescape.common.ControllerTest;
import roomescape.reservation.application.ThemeService;
import roomescape.reservation.dto.request.ThemeSaveRequest;
import roomescape.reservation.dto.response.ThemeResponse;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static roomescape.TestFixture.*;

@WebMvcTest(ThemeController.class)
class ThemeControllerTest extends ControllerTest {
    @MockBean
    protected ThemeService themeService;

    @Test
    @DisplayName("테마 생성 POST 요청 시 상태코드 201을 반환한다.")
    void createTheme() throws Exception {
        // given
        ThemeSaveRequest request = new ThemeSaveRequest(WOOTECO_THEME_NAME, WOOTECO_THEME_DESCRIPTION, THEME_THUMBNAIL);
        ThemeResponse expectedResponse = new ThemeResponse(1L, WOOTECO_THEME_NAME, WOOTECO_THEME_DESCRIPTION, THEME_THUMBNAIL);

        BDDMockito.given(themeService.create(any()))
                .willReturn(expectedResponse);

        // when & then
        mockMvc.perform(post("/themes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(request)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value(WOOTECO_THEME_NAME))
                .andExpect(jsonPath("$.description").value(WOOTECO_THEME_DESCRIPTION))
                .andExpect(jsonPath("$.thumbnail").value(THEME_THUMBNAIL))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("테마 목록 GET 요청 시 상태코드 200을 반환한다.")
    void findAllThemes() throws Exception {
        // given
        ThemeResponse expectedResponse = new ThemeResponse(1L, WOOTECO_THEME_NAME, WOOTECO_THEME_DESCRIPTION, THEME_THUMBNAIL);

        BDDMockito.given(themeService.findAll())
                .willReturn(List.of(expectedResponse));

        // when & then
        mockMvc.perform(get("/themes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(WOOTECO_THEME_NAME))
                .andExpect(jsonPath("$[0].description").value(WOOTECO_THEME_DESCRIPTION))
                .andExpect(jsonPath("$[0].thumbnail").value(THEME_THUMBNAIL))
                .andExpect(jsonPath("$[0].id").value(1L));
    }

    @Test
    @DisplayName("테마 DELETE 요청 시 상태 코드 204를 반환한다.")
    void deleteTheme() throws Exception {
        // given
        BDDMockito.willDoNothing()
                .given(themeService)
                .deleteById(anyLong());

        // when & then
        mockMvc.perform(delete("/themes/{id}", anyLong()))
                .andDo(print())
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("최근 일주일 인기 테마 목록 GET 요청 시 상태코드 200을 반환한다.")
    void findAllPopular() throws Exception {
        // given
        ThemeResponse expectedWootecoResponse = new ThemeResponse(1L, WOOTECO_THEME_NAME, WOOTECO_THEME_DESCRIPTION, THEME_THUMBNAIL);
        ThemeResponse expectedHorrorResponse = new ThemeResponse(2L, HORROR_THEME_NAME, HORROR_THEME_DESCRIPTION, THEME_THUMBNAIL);

        BDDMockito.given(themeService.findAllPopular())
                .willReturn(List.of(expectedWootecoResponse, expectedHorrorResponse));

        // when & then
        mockMvc.perform(get("/themes/popular")
                        .contentType(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value(WOOTECO_THEME_NAME))
                .andExpect(jsonPath("$[0].description").value(WOOTECO_THEME_DESCRIPTION))
                .andExpect(jsonPath("$[0].thumbnail").value(THEME_THUMBNAIL))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].name").value(HORROR_THEME_NAME))
                .andExpect(jsonPath("$[1].description").value(HORROR_THEME_DESCRIPTION))
                .andExpect(jsonPath("$[1].thumbnail").value(THEME_THUMBNAIL))
                .andExpect(jsonPath("$[1].id").value(2L));
    }
}
