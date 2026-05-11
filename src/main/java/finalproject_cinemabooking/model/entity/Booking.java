package finalproject_cinemabooking.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {

    // Trạng thái của đơn đặt vé
    public enum BookingStatus {
        CONFIRMED, CANCELLED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Mã đơn đặt vé

    @Column(nullable = false, unique = true)
    private String bookingCode; // Mã tra cứu đơn hàng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user; // Người dùng nào thực hiện đặt vé

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "showtime_id", nullable = false)
    private Showtime showtime; // Thuộc suất chiếu nào

    @Column(nullable = false)
    private Double totalPrice; // Tổng tiền thanh toán cho đơn hàng

    @Column(nullable = false)
    private LocalDateTime bookingTime; // Thời điểm thực hiện bấm đặt vé

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status; // Trạng thái của đơn: CHỜ, ĐÃ XÁC NHẬN, ĐÃ HỦY
}
