package finalproject_cinemabooking.service.impl;

import finalproject_cinemabooking.model.dto.BookingHistoryDTO;
import finalproject_cinemabooking.model.entity.*;
import finalproject_cinemabooking.repository.*;
import finalproject_cinemabooking.service.BookingService;
import jakarta.persistence.LockModeType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired private BookingRepository bookingRepository;
    @Autowired private TicketRepository ticketRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private ShowtimeRepository showtimeRepository;
    @Autowired private SeatRepository seatRepository;

    @Override
    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    public Booking createBooking(Long userId, Long showtimeId, List<Long> seatIds) {
        if (seatIds == null || seatIds.isEmpty()) {
            throw new RuntimeException("Vui lòng chọn ít nhất 1 ghế.");
        }
        if (seatIds.size() > 6) {
            throw new RuntimeException("Số lượng ghế đặt quá nhiều. Vui lòng liên hệ admin.");
        }
        User user = userRepository.findById(userId).orElseThrow();
        Showtime showtime = showtimeRepository.findByIdWithMovieAndRoom(showtimeId).orElseThrow();

        if (!showtime.getStartTime().isAfter(LocalDateTime.now())) {
            throw new RuntimeException("Suất chiếu đã bắt đầu, không thể đặt vé.");
        }

        List<Ticket> existingTickets = ticketRepository.findConfirmedTicketsByShowtime(showtimeId);
        List<Long> bookedSeatIds = existingTickets.stream().map(t -> t.getSeat().getId()).toList();
        for (Long seatId : seatIds) {
            if (bookedSeatIds.contains(seatId)) {
                throw new RuntimeException("Một hoặc nhiều ghế đã được đặt bởi người khác.");
            }
            Seat seat = seatRepository.findById(seatId).orElseThrow();
            if (!seat.getRoom().getId().equals(showtime.getRoom().getId())) {
                throw new RuntimeException("Ghế không thuộc phòng chiếu của suất này.");
            }
        }

        double pricePerSeat = 100000;
        Booking booking = new Booking();
        booking.setUser(user);
        booking.setShowtime(showtime);
        booking.setBookingTime(LocalDateTime.now());
        booking.setTotalPrice(pricePerSeat * seatIds.size());
        booking.setStatus(Booking.BookingStatus.CONFIRMED);
        
        // Generate a random 8-character alphanumeric booking code
        String code = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        booking.setBookingCode(code);
        
        Booking savedBooking = bookingRepository.save(booking);

        for (Long seatId : seatIds) {
            Seat seat = seatRepository.findById(seatId).orElseThrow();
            Ticket ticket = new Ticket();
            ticket.setBooking(savedBooking);
            ticket.setSeat(seat);
            ticket.setShowtime(showtime);
            ticket.setPrice(pricePerSeat);
            ticket.setStatus(Ticket.TicketStatus.CONFIRMED); // Sửa lại cú pháp cho đúng
            ticketRepository.save(ticket);
        }
        return savedBooking;
    }

    @Override
    @Transactional
    public void cancelBooking(Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy hóa đơn đặt vé."));

        LocalDateTime limitTime = booking.getShowtime().getStartTime().minusHours(1);

        if (LocalDateTime.now().isAfter(limitTime)) {
            throw new RuntimeException("Chỉ được hủy vé trước 1 giờ so với giờ chiếu!");
        }

        booking.setStatus(Booking.BookingStatus.CANCELLED);
        bookingRepository.save(booking);

        List<Ticket> ticketsToCancel = ticketRepository.findByBookingId(bookingId);
        
        for (Ticket ticket : ticketsToCancel) {
            ticket.setStatus(Ticket.TicketStatus.CANCELLED); // Sửa lại cú pháp cho đúng
        }
        ticketRepository.saveAll(ticketsToCancel);
    }

    @Override
    public List<BookingHistoryDTO> getBookingHistory(Long userId) {
        List<Booking> bookings = bookingRepository.findHistoryWithShowtimeAndMovie(userId);
        List<BookingHistoryDTO> dtos = new ArrayList<>();
        for (Booking b : bookings) {
            BookingHistoryDTO dto = new BookingHistoryDTO();
            dto.setId(b.getId());
            dto.setBookingCode(b.getBookingCode()); // Map mã đơn đặt vé
            dto.setMovieTitle(b.getShowtime().getMovie().getTitle());
            dto.setMoviePosterUrl(b.getShowtime().getMovie().getPosterUrl());
            dto.setRoomName(b.getShowtime().getRoom().getName());
            dto.setBookingTime(b.getBookingTime());
            dto.setShowtimeStart(b.getShowtime().getStartTime());
            dto.setTotalPrice(b.getTotalPrice());
            dto.setStatus(b.getStatus().toString());
            
            List<Ticket> tickets = ticketRepository.findByBookingId(b.getId());
            dto.setSeatNames(tickets.stream().map(t -> t.getSeat().getSeatName()).toList());

            dtos.add(dto);
        }
        return dtos;
    }

    @Override
    public BookingHistoryDTO getBookingByCode(String bookingCode) {
        Booking b = bookingRepository.findByBookingCodeWithDetails(bookingCode)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn đặt vé với mã: " + bookingCode));
                
        BookingHistoryDTO dto = new BookingHistoryDTO();
        dto.setId(b.getId());
        dto.setBookingCode(b.getBookingCode());
        dto.setMovieTitle(b.getShowtime().getMovie().getTitle());
        dto.setMoviePosterUrl(b.getShowtime().getMovie().getPosterUrl());
        dto.setRoomName(b.getShowtime().getRoom().getName());
        dto.setBookingTime(b.getBookingTime());
        dto.setShowtimeStart(b.getShowtime().getStartTime());
        dto.setTotalPrice(b.getTotalPrice());
        dto.setStatus(b.getStatus().toString());
        
        List<Ticket> tickets = ticketRepository.findByBookingId(b.getId());
        dto.setSeatNames(tickets.stream().map(t -> t.getSeat().getSeatName()).toList());
        
        return dto;
    }
}
