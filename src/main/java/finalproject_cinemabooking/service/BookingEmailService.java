package finalproject_cinemabooking.service;

import finalproject_cinemabooking.model.entity.Booking;
import finalproject_cinemabooking.model.entity.Ticket;
import finalproject_cinemabooking.model.entity.UserProfile;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BookingEmailService {
    private static final DateTimeFormatter SHOWTIME_FORMAT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:}")
    private String fromAddress;

    public BookingEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendBookingSuccessEmail(UserProfile profile, Booking booking, List<Ticket> tickets) {
        if (profile == null || profile.getEmail() == null || profile.getEmail().trim().isEmpty()) {
            return;
        }

        String subject = "Đặt vé thành công - " + booking.getBookingCode();
        String body = buildEmailBody(profile, booking, tickets);

        SimpleMailMessage message = new SimpleMailMessage();
        if (fromAddress != null && !fromAddress.trim().isEmpty()) {
            message.setFrom(fromAddress.trim());
        }
        message.setTo(profile.getEmail().trim());
        message.setSubject(subject);
        message.setText(body);

        mailSender.send(message);
    }

    private String buildEmailBody(UserProfile profile, Booking booking, List<Ticket> tickets) {
        String seatNames = tickets == null ? "" : tickets.stream()
                .map(ticket -> ticket.getSeat() == null ? "" : ticket.getSeat().getSeatName())
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.joining(", "));

        String showtimeText = booking.getShowtime() == null || booking.getShowtime().getStartTime() == null
                ? ""
                : booking.getShowtime().getStartTime().format(SHOWTIME_FORMAT);

        String movieTitle = booking.getShowtime() == null || booking.getShowtime().getMovie() == null
                ? ""
                : booking.getShowtime().getMovie().getTitle();

        String roomName = booking.getShowtime() == null || booking.getShowtime().getRoom() == null
                ? ""
                : booking.getShowtime().getRoom().getName();

        return "Đặt vé thành công!\n"
                + "Khách hàng: " + (profile.getFullName() == null ? "" : profile.getFullName()) + "\n"
                + "Mã đặt vé: " + booking.getBookingCode() + "\n"
                + "Phim: " + movieTitle + "\n"
                + "Phòng: " + roomName + "\n"
                + "Ghế: " + seatNames + "\n"
                + "Thời gian chiếu: " + showtimeText + "\n"
                + "Tổng tiền: " + (booking.getTotalPrice() == null ? 0d : booking.getTotalPrice()) + "\n";
    }
}
