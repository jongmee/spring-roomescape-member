package roomescape.dto;

import roomescape.domain.Reservation;
import roomescape.domain.ReservationTime;
import roomescape.domain.Theme;

import java.time.LocalDate;

public record ReservationSaveRequest(
        String name,
        LocalDate date,
        Long timeId,
        Long themeId) {

    public Reservation toModel(ThemeResponse themeResponse, ReservationTimeResponse timeResponse) {
        ReservationTime time = new ReservationTime(timeResponse.id(), timeResponse.startAt());
        Theme theme = new Theme(themeResponse.id(), themeResponse.name(), themeResponse.description(), themeResponse.thumbnail());
        return new Reservation(name, date, time, theme);
    }
}
