package finalproject_cinemabooking.model.dto;

import lombok.Data;
import java.util.List;

@Data
public class BookingRequestDTO {
    private Long showtimeId;
    private List<Long> seatIds;
}

