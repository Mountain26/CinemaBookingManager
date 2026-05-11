package finalproject_cinemabooking.repository;

import finalproject_cinemabooking.model.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    // Lấy danh sách tất cả các ghế theo ID phòng chiếu
    List<Seat> findByRoomId(Long roomId);
}

