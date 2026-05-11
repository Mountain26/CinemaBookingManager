package finalproject_cinemabooking.repository;

import finalproject_cinemabooking.model.entity.Booking;
import finalproject_cinemabooking.model.dto.MonthlyRevenueView;
import finalproject_cinemabooking.model.dto.TopMovieRevenueView;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByUserId(Long userId);

    @Query("SELECT DISTINCT b FROM Booking b " +
            "JOIN FETCH b.showtime s " +
            "JOIN FETCH s.movie " +
            "JOIN FETCH s.room " +
            "WHERE b.user.id = :userId")
    List<Booking> findHistoryWithShowtimeAndMovie(@Param("userId") Long userId);

    @Query("SELECT DISTINCT b FROM Booking b " +
            "JOIN FETCH b.showtime s " +
            "JOIN FETCH s.movie " +
            "JOIN FETCH s.room " +
            "JOIN FETCH b.user " +
            "WHERE b.bookingCode = :bookingCode")
    Optional<Booking> findByBookingCodeWithDetails(@Param("bookingCode") String bookingCode);

    @Modifying
    @Query("DELETE FROM Booking b WHERE b.showtime.id = :showtimeId")
    void deleteByShowtimeId(@Param("showtimeId") Long showtimeId);

    @Query("SELECT FUNCTION('DATE_FORMAT', b.bookingTime, '%Y-%m') AS month, SUM(b.totalPrice) AS total " +
            "FROM Booking b WHERE b.status = 'CONFIRMED' " +
            "GROUP BY FUNCTION('DATE_FORMAT', b.bookingTime, '%Y-%m') " +
            "ORDER BY FUNCTION('DATE_FORMAT', b.bookingTime, '%Y-%m')")
    List<MonthlyRevenueView> findMonthlyRevenue();

    @Query("SELECT b.showtime.movie.title AS title, SUM(b.totalPrice) AS total " +
            "FROM Booking b WHERE b.status = 'CONFIRMED' " +
            "GROUP BY b.showtime.movie.id, b.showtime.movie.title " +
            "ORDER BY SUM(b.totalPrice) DESC")
    List<TopMovieRevenueView> findTopMoviesByRevenue(Pageable pageable);

    @Query("SELECT SUM(b.totalPrice) FROM Booking b WHERE b.status = 'CONFIRMED' AND b.bookingTime >= :startTime")
    Double sumRevenueSince(@Param("startTime") java.time.LocalDateTime startTime);
}
