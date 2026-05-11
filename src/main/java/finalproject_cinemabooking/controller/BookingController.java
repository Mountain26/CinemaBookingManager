package finalproject_cinemabooking.controller;

import finalproject_cinemabooking.model.dto.BookingHistoryDTO;
import finalproject_cinemabooking.model.dto.BookingRequestDTO;
import finalproject_cinemabooking.service.BookingService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {

    @Autowired
    private BookingService bookingService;

    @PostMapping("/create")
    public ResponseEntity<?> createBooking(@RequestBody BookingRequestDTO request, HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) return ResponseEntity.status(401).body("Chưa đăng nhập!");

        try {
            bookingService.createBooking(userId, request.getShowtimeId(), request.getSeatIds());
            return ResponseEntity.ok("Đặt vé thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/cancel/{bookingId}")
    public ResponseEntity<?> cancelBooking(@PathVariable Long bookingId) {
        try {
            bookingService.cancelBooking(bookingId);
            return ResponseEntity.ok("Huỷ vé thành công!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/history")
    public ResponseEntity<?> getHistory(HttpSession session) {
        Long userId = (Long) session.getAttribute("USER_ID");
        if (userId == null) return ResponseEntity.status(401).body("Chưa đăng nhập!");

        List<BookingHistoryDTO> history = bookingService.getBookingHistory(userId);
        return ResponseEntity.ok(history);
    }
    
    @GetMapping("/search")
    public ResponseEntity<?> searchBookingByCode(@RequestParam String code) {
        try {
            BookingHistoryDTO dto = bookingService.getBookingByCode(code);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
