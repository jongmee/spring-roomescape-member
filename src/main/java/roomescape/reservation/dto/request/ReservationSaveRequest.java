package roomescape.reservation.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import roomescape.reservation.domain.Reservation;
import roomescape.reservation.domain.ReservationTime;
import roomescape.reservation.domain.Theme;
import roomescape.reservation.dto.response.ReservationTimeResponse;
import roomescape.reservation.dto.response.ThemeResponse;

import java.time.LocalDate;

public record ReservationSaveRequest(
        @NotBlank(message = "예약자 이름은 비어있을 수 없습니다.")
        String name,
        @NotNull(message = "예약 날짜는 비어있을 수 없습니다.")
        LocalDate date,
        @NotNull(message = "예약 시간 Id는 비어있을 수 없습니다.")
        Long timeId,
        @NotNull(message = "테마 Id는 비어있을 수 없습니다.")
        Long themeId) {

    public Reservation toModel(ThemeResponse themeResponse, ReservationTimeResponse timeResponse) {
        ReservationTime time = new ReservationTime(timeResponse.id(), timeResponse.startAt());
        Theme theme = new Theme(themeResponse.id(), themeResponse.name(), themeResponse.description(), themeResponse.thumbnail());
        return new Reservation(name, date, time, theme);
    }
}