package finalproject_cinemabooking.service;

import finalproject_cinemabooking.model.entity.Booking;
import finalproject_cinemabooking.model.entity.Movie;
import finalproject_cinemabooking.model.entity.Room;
import finalproject_cinemabooking.model.entity.Seat;
import finalproject_cinemabooking.model.entity.Showtime;
import finalproject_cinemabooking.model.entity.Ticket;
import finalproject_cinemabooking.model.entity.UserProfile;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;

import java.time.LocalDateTime;
import java.util.List;

class BookingEmailServiceTest {

    @Test
    void sendBookingSuccessEmail_sendsMailWithFakeQr() {
        JavaMailSender mailSender = Mockito.mock(JavaMailSender.class);
        BookingEmailService service = new BookingEmailService(mailSender);

        UserProfile profile = new UserProfile();
        profile.setEmail("user@example.com");
        profile.setFullName("Test User");

        Movie movie = new Movie();
        movie.setTitle("Movie A");

        Room room = new Room();
        room.setName("Room 1");

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(LocalDateTime.of(2026, 5, 12, 10, 0));

        Booking booking = new Booking();
        booking.setBookingCode("ABC12345");
        booking.setShowtime(showtime);
        booking.setTotalPrice(200000d);

        Seat seat = new Seat();
        seat.setSeatName("A1");

        Ticket ticket = new Ticket();
        ticket.setSeat(seat);

        service.sendBookingSuccessEmail(profile, booking, List.of(ticket));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        Mockito.verify(mailSender).send(captor.capture());

        SimpleMailMessage message = captor.getValue();
        Assertions.assertEquals("user@example.com", message.getTo()[0]);
        Assertions.assertTrue(message.getText().contains("ABC12345"));
        Assertions.assertTrue(message.getText().contains("Đặt vé thành công"));
        Assertions.assertFalse(message.getText().contains("QR giả lập"));
    }
}
