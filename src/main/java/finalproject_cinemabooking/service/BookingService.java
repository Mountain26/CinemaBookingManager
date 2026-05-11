package finalproject_cinemabooking.service;

import finalproject_cinemabooking.model.dto.BookingHistoryDTO;
import finalproject_cinemabooking.model.entity.Booking;
import java.util.List;

public interface BookingService {
    Booking createBooking(Long userId, Long showtimeId, List<Long> seatIds);
    void cancelBooking(Long bookingId);
    List<BookingHistoryDTO> getBookingHistory(Long userId);
    BookingHistoryDTO getBookingByCode(String bookingCode);
}
