package finalproject_cinemabooking.repository;

import finalproject_cinemabooking.model.entity.Showtime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ShowtimeRepository extends JpaRepository<Showtime, Long> {
    List<Showtime> findByStatusNotOrderByStartTimeAsc(Showtime.ShowtimeStatus status);
    List<Showtime> findAllByOrderByStartTimeAsc();

    // Kiểm tra xem phòng có bị trùng lịch chiếu không
    // Trùng lịch nếu (thời gian bắt đầu mới < thời gian kết thúc cũ) VÀ (thời gian kết thúc mới > thời gian bắt đầu cũ)
    @Query("SELECT s FROM Showtime s WHERE s.room.id = :roomId AND (:newStart < s.endTime AND :newEnd > s.startTime)")
    List<Showtime> findConflictingShowtimes(@Param("roomId") Long roomId,
                                            @Param("newStart") LocalDateTime newStart,
                                            @Param("newEnd") LocalDateTime newEnd);

    @Query("SELECT s FROM Showtime s LEFT JOIN FETCH s.movie LEFT JOIN FETCH s.room WHERE s.id = :id")
    Optional<Showtime> findByIdWithMovieAndRoom(@Param("id") Long id);

    @Query("SELECT s FROM Showtime s JOIN FETCH s.movie JOIN FETCH s.room WHERE s.movie.id = :movieId AND s.status <> 'DELETED' ORDER BY s.startTime ASC")
    List<Showtime> findByMovieIdOrderByStartTime(@Param("movieId") Long movieId);

    List<Showtime> findByMovieIdAndStatusNot(Long movieId, Showtime.ShowtimeStatus status);

    @Query("SELECT COUNT(DISTINCT s.movie.id) FROM Showtime s WHERE s.status <> 'DELETED'")
    long countActiveMovies();

    boolean existsByRoomId(Long roomId);
}
