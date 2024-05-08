package roomescape.reservation.domain;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import roomescape.common.RepositoryTest;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.Time;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static roomescape.TestFixture.*;

class ReservationRepositoryTest extends RepositoryTest {
    @Autowired
    private ReservationRepository reservationRepository;

    @BeforeEach
    void setUp() {
        String insertTimeSql = "INSERT INTO reservation_time (start_at) VALUES (?)";
        jdbcTemplate.update(insertTimeSql, Time.valueOf(MIA_RESERVATION_TIME));
        String insertThemeSql = "INSERT INTO theme (name, description, thumbnail) VALUES (?, ?, ?)";
        jdbcTemplate.update(insertThemeSql, WOOTECO_THEME_NAME, WOOTECO_THEME_DESCRIPTION, THEME_THUMBNAIL);
    }

    @Test
    @DisplayName("예약을 저장한다.")
    void save() {
        // given
        Long timeId = 1L;
        Reservation reservation = MIA_RESERVATION(new ReservationTime(timeId, MIA_RESERVATION_TIME), WOOTECO_THEME());

        // when
        Reservation savedReservation = reservationRepository.save(reservation);

        // then
        assertThat(savedReservation.getId()).isNotNull();
    }

    @Test
    @DisplayName("동일 시간대의 예약이 존재하는지 조회한다.")
    void existByDateAndTimeIdAndThemeId() {
        // given
        Long timeId = 1L;
        Long themeId = 1L;
        String insertSql = "INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertSql, MIA_NAME, Date.valueOf(MIA_RESERVATION_DATE), timeId, themeId);

        // when
        boolean existByDateAndTimeIdAndThemeId = reservationRepository.existByDateAndTimeIdAndThemeId(
                MIA_RESERVATION_DATE, timeId, themeId);

        // then
        assertThat(existByDateAndTimeIdAndThemeId).isTrue();
    }

    @Test
    @DisplayName("모든 예약 목록을 조회한다.")
    void findAll() {
        // given
        Long timeId = 1L;
        Long themeId = 1L;
        String insertSql = "INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?)";
        jdbcTemplate.update(insertSql, MIA_NAME, MIA_RESERVATION_DATE, timeId, themeId);

        // when
        List<Reservation> reservations = reservationRepository.findAll();

        // then
        Integer count = jdbcTemplate.queryForObject("SELECT count(1) from reservation", Integer.class);
        assertAll(() -> {
            assertThat(reservations.size()).isEqualTo(count);
            assertThat(reservations).extracting(Reservation::getTheme)
                    .extracting(Theme::getName)
                    .containsExactly(WOOTECO_THEME_NAME);
            assertThat(reservations).extracting(Reservation::getTime)
                    .extracting(ReservationTime::getStartAt)
                    .containsExactly(MIA_RESERVATION_TIME);
        });
    }

    @Test
    @DisplayName("Id로 예약을 삭제한다.")
    void deleteById() {
        // given
        long timeId = 1L;
        String insertSql = "INSERT INTO reservation (name, date, time_id) VALUES (?, ?, ?)";
        KeyHolder keyHolder = new GeneratedKeyHolder();
        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(insertSql, new String[]{"id"});
            ps.setString(1, MIA_NAME);
            ps.setDate(2, Date.valueOf(MIA_RESERVATION_DATE));
            ps.setLong(3, timeId);
            return ps;
        }, keyHolder);
        Long id = keyHolder.getKey().longValue();

        // when
        reservationRepository.deleteById(id);

        // then
        Integer count = jdbcTemplate.queryForObject("SELECT count(1) from reservation where id = ?", Integer.class, id);
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("timeId에 해당하는 예약 건수를 조회한다.")
    void countByTimeId() {
        // given
        long timeId = 2L;

        // when
        int count = reservationRepository.countByTimeId(timeId);

        // then
        assertThat(count).isEqualTo(0);
    }

    @Test
    @DisplayName("날짜와 themeId로 예약 목록을 조회한다.")
    void findAllByDateAndThemeId() {
        // given
        Long timeId = 1L;
        Long themeId = 1L;
        String insertSql = "INSERT INTO reservation (name, date, time_id, theme_id) VALUES (?, ?, ?, ?), (?, ?, ?, ?)";
        jdbcTemplate.update(
                insertSql,
                MIA_NAME, Date.valueOf(MIA_RESERVATION_DATE), timeId, themeId,
                TOMMY_NAME, Date.valueOf(MIA_RESERVATION_DATE), timeId, themeId
        );

        // when
        List<Long> reservationsByDateAndThemeId = reservationRepository.findAllTimeIdsByDateAndThemeId(MIA_RESERVATION_DATE, themeId);

        // then
        assertThat(reservationsByDateAndThemeId).hasSize(2);
    }
}
