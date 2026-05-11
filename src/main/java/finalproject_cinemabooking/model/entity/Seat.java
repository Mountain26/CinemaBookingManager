package finalproject_cinemabooking.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "seats")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Seat {

    // Trạng thái của ghế ngồi
    public enum SeatStatus {
        AVAILABLE, BOOKED, MAINTENANCE
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Mã ghế tự động tăng

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room; // Liên kết nhiều ghế với 1 phòng chiếu

    @Column(nullable = false)
    private String seatName; // Tên ghế (Ví dụ: A1, A2, B1...)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status; // Trạng thái ghế: TRỐNG, ĐÃ ĐẶT...
}


