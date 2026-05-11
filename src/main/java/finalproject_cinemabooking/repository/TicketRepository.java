package finalproject_cinemabooking.repository;

import finalproject_cinemabooking.model.entity.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TicketRepository extends JpaRepository<Ticket, Long> {

    // Lấy danh sách các vé theo ID của suất chiếu để biết ghế nào đã được đặt
    @Query("SELECT t FROM Ticket t WHERE t.booking.showtime.id = :showtimeId AND t.booking.status = 'CONFIRMED'")
    List<Ticket> findConfirmedTicketsByShowtime(@Param("showtimeId") Long showtimeId);

    @Query("SELECT t FROM Ticket t WHERE t.booking.id = :bookingId")
    List<Ticket> findByBookingId(@Param("bookingId") Long bookingId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.booking.showtime.id = :showtimeId AND t.booking.status = 'CONFIRMED'")
    long countConfirmedTicketsByShowtime(@Param("showtimeId") Long showtimeId);

    @Modifying
    @Query("DELETE FROM Ticket t WHERE t.showtime.id = :showtimeId")
    void deleteByShowtimeId(@Param("showtimeId") Long showtimeId);

    @Query("SELECT COUNT(t) FROM Ticket t WHERE t.booking.status = 'CONFIRMED' AND t.booking.bookingTime >= :startTime")
    long countConfirmedTicketsSince(@Param("startTime") LocalDateTime startTime);
}
