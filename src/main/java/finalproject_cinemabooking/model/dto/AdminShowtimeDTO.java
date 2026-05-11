package finalproject_cinemabooking.model.dto;

import finalproject_cinemabooking.model.entity.Showtime;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminShowtimeDTO {
    private Showtime showtime;
    private boolean deletable;
    private long bookedSeatsCount;
}
