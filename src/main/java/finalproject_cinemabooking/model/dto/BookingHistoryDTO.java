package finalproject_cinemabooking.model.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class BookingHistoryDTO {
    private Long id;
    private String bookingCode; // Thêm mã đơn đặt vé
    private String movieTitle;
    private String moviePosterUrl;
    private String roomName;
    private LocalDateTime bookingTime;
    private LocalDateTime showtimeStart;
    private Double totalPrice;
    private String status;
    private List<String> seatNames;
}
