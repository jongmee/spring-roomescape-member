package roomescape.repository;

import roomescape.domain.Theme;

import java.util.List;
import java.util.Optional;

public interface ThemeRepository {

    Theme save(Theme theme);

    List<Theme> findAll();

    Optional<Theme> findById(Long id);

    void deleteById(Long id);

    List<Theme> findAllOrderByReservationCountDaysAgo(int days, int limit);
}
