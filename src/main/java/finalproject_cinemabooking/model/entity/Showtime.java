package finalproject_cinemabooking.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "showtimes")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Showtime {

    // Trạng thái của suất chiếu
    public enum ShowtimeStatus {
        SCHEDULED, SOLD_OUT, ENDED, DELETED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Mã suất chiếu

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "movie_id", nullable = false)
    private Movie movie; // Phim nào được trình chiếu

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", nullable = false)
    private Room room; // Phòng chiếu nào

    @Column(nullable = false)
    private LocalDateTime startTime; // Thời gian bắt đầu chiếu

    @Column(nullable = false)
    private LocalDateTime endTime; // Thời gian kết thúc (Tự động tính: Bắt đầu + Thời lượng + 15p dọn phòng)

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ShowtimeStatus status; // Trạng thái của suất chiếu
}


