package roomescape.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcInsert;
import roomescape.domain.Theme;

import java.sql.Time;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static roomescape.TestFixture.*;

class ThemeRepositoryTest extends RepositoryTest {
    @Autowired
    private ThemeRepository themeRepository;

    private SimpleJdbcInsert jdbcInsert;

    @BeforeEach
    void setUp() {
        jdbcInsert = new SimpleJdbcInsert(jdbcTemplate)
                .withTableName("theme")
                .usingGeneratedKeyColumns("id");
    }

    @Test
    @DisplayName("테마를 저장한다.")
    void save() {
        // given
        Theme theme = new Theme("레벨2 탈출", "우테코 레벨2를 탈출하는 내용입니다.", "https://i.pinimg.com/236x/6e/bc/46/6ebc461a94a49f9ea3b8bbe2204145d4.jpg");

        // when
        Theme savedTheme = themeRepository.save(theme);

        // then
        assertThat(savedTheme.getId()).isNotNull();
    }

    @Test
    @DisplayName("테마 목록을 조회한다.")
    void findAll() {
        // given
        String insertSql = "INSERT INTO theme (name, description, thumbnail) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertSql, WOOTECO_THEME_NAME, WOOTECO_THEME_DESCRIPTION, THEME_THUMBNAIL);

        // when
        List<Theme> themes = themeRepository.findAll();

        // then
        assertThat(themes).hasSize(1);
    }

    @Test
    @DisplayName("Id로 테마를 조회한다.")
    void findById() {
        // given
        Theme theme = WOOTECO_THEME();
        SqlParameterSource params = new BeanPropertySqlParameterSource(theme);
        Long id = jdbcInsert.executeAndReturnKey(params).longValue();

        // when
        Optional<Theme> foundTheme = themeRepository.findById(id);

        // then
        assertThat(foundTheme).isNotEmpty();
    }

    @Test
    @DisplayName("존재하지 않는 Id로 테마를 조회하면 빈 Optional을 반환한다.")
    void findByNotExistingId() {
        // given
        Long notExistingId = 1L;

        // when
        Optional<Theme> foundTheme = themeRepository.findById(notExistingId);

        // then
        assertThat(foundTheme).isEmpty();
    }

    @Test
    @DisplayName("Id로 테마를 삭제한다.")
    void deleteById() {
        // given
        Theme theme = WOOTECO_THEME();
        SqlParameterSource params = new BeanPropertySqlParameterSource(theme);
        Long id = jdbcInsert.executeAndReturnKey(params).longValue();

        // when
        themeRepository.deleteById(id);

        // then
        Integer count = jdbcTemplate.queryForObject("SELECT count(1) from theme where id = ?", Integer.class, id);
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("최근 일주일을 기준으로 예약이 많은 순으로 테마 10개를 조회한다.")
    void findAllOrderByReservationCountInLastWeek() {
        // given
        String insertTimeSql = "INSERT INTO reservation_time (start_at) VALUES (?), (?)";
        jdbcTemplate.update(insertTimeSql,
                Time.valueOf(MIA_RESERVATION_TIME),
                Time.valueOf(TOMMY_RESERVATION_TIME));
        String insertThemeSql = "INSERT INTO theme (name, description, thumbnail) VALUES (?, ?, ?), (?, ?, ?)";
        jdbcTemplate.update(insertThemeSql,
                WOOTECO_THEME_NAME, WOOTECO_THEME_DESCRIPTION, THEME_THUMBNAIL,
                HORROR_THEME_NAME, HORROR_THEME_DESCRIPTION, THEME_THUMBNAIL);
        String insertReservationSql = "INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?), (?, ?, ?, ?), (?, ?, ?, ?)";
        jdbcTemplate.update(insertReservationSql,
                USER_MIA, MIA_RESERVATION_DATE, 1L, 1L,
                USER_TOMMY, TOMMY_RESERVATION_DATE, 2L, 1L,
                "냥", "2030-05-03", 1L, 2L);

        // when
        List<Theme> allOrderByReservationCountInLastWeek = themeRepository.findAllOrderByReservationCountDaysAgo(7);

        // then
        assertThat(allOrderByReservationCountInLastWeek).extracting(Theme::getName)
                .containsExactly(WOOTECO_THEME_NAME, HORROR_THEME_NAME);
    }
}
