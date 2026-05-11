package finalproject_cinemabooking.model.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ShowtimeCreateDTO {
    private Long movieId;
    private Long roomId;
    private LocalDateTime startTime;
}

