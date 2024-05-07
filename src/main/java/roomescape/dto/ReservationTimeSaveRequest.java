package roomescape.dto;

import jakarta.validation.constraints.NotNull;
import roomescape.domain.ReservationTime;

import java.time.LocalTime;

public record ReservationTimeSaveRequest(
        @NotNull(message = "예약 시간은 비어있을 수 없습니다.")
        LocalTime startAt
) {

    public ReservationTime toModel() {
        return new ReservationTime(startAt);
    }
}
