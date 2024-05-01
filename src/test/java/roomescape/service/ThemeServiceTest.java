package roomescape.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import roomescape.domain.Theme;
import roomescape.dto.ThemeResponse;
import roomescape.exception.NotFoundException;
import roomescape.repository.ThemeRepository;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static roomescape.TestFixture.WOOTECO_THEME;

@ExtendWith(MockitoExtension.class)
class ThemeServiceTest {
    @Mock
    private ThemeRepository themeRepository;

    @InjectMocks
    private ThemeService themeService;

    @Test
    @DisplayName("테마를 생성한다.")
    void create() {
        // given
        Theme expectedTheme = WOOTECO_THEME(1L);

        BDDMockito.given(themeRepository.save(any()))
                .willReturn(expectedTheme);

        // when
        ThemeResponse response = themeService.create(expectedTheme);

        // then
        assertThat(response.id()).isEqualTo(expectedTheme.getId());
    }

    @Test
    @DisplayName("테마 목록을 조회한다.")
    void findAll() {
        // given
        List<Theme> expectedThemes = List.of(WOOTECO_THEME(1L));

        BDDMockito.given(themeRepository.findAll())
                .willReturn(expectedThemes);

        // when
        List<ThemeResponse> responses = themeService.findAll();

        // then
        ThemeResponse expectedResponse = ThemeResponse.from(WOOTECO_THEME(1L));
        assertThat(responses).hasSize(1)
                .containsExactly(expectedResponse);
    }

    @Test
    @DisplayName("Id로 테마를 조회한다.")
    void findById() {
        // given
        Theme expectedTheme = WOOTECO_THEME(1L);

        BDDMockito.given(themeRepository.findById(anyLong()))
                .willReturn(Optional.of(expectedTheme));

        // when
        ThemeResponse themeResponse = themeService.findById(1L);

        // then
        assertAll(() -> {
            assertThat(themeResponse.id()).isEqualTo(expectedTheme.getId());
            assertThat(themeResponse.name()).isEqualTo(expectedTheme.getName());
        });
    }

    @Test
    @DisplayName("조회하려는 테마가 존재하지 않는 경우 예외가 발생한다.")
    void findByNotExistId() {
        // given
        BDDMockito.given(themeRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> themeService.findById(1L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    @DisplayName("테마를 삭제한다.")
    void deleteById() {
        // given
        Theme theme = WOOTECO_THEME(1L);

        BDDMockito.given(themeRepository.findById(anyLong()))
                .willReturn(Optional.of(theme));

        // when & then
        assertThatCode(() -> themeService.deleteById(1L))
                .doesNotThrowAnyException();
    }

    @Test
    @DisplayName("삭제하려는 테마가 존재하지 않는 경우 예외가 발생한다.")
    void deleteByNotExistId() {
        // given
        BDDMockito.given(themeRepository.findById(anyLong()))
                .willReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> themeService.deleteById(1L))
                .isInstanceOf(NotFoundException.class);
    }
}