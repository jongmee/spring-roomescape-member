package roomescape.reservation.domain;

import roomescape.reservation.domain.Reservation;

import java.time.LocalDate;
import java.util.List;

public interface ReservationRepository {

    Reservation save(Reservation reservation);

    boolean existByDateAndTimeIdAndThemeId(LocalDate date, Long timeId, Long themeId);

    List<Reservation> findAll();

    void deleteById(Long id);

    int countByTimeId(Long timeId);

    List<Long> findAllTimeIdsByDateAndThemeId(LocalDate date, Long themeId);
}
