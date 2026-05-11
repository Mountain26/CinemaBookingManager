package finalproject_cinemabooking.service.impl;

import finalproject_cinemabooking.model.dto.AdminShowtimeDTO;
import finalproject_cinemabooking.model.dto.ShowtimeCreateDTO;
import finalproject_cinemabooking.model.entity.Movie;
import finalproject_cinemabooking.model.entity.Room;
import finalproject_cinemabooking.model.entity.Showtime;
import finalproject_cinemabooking.repository.MovieRepository;
import finalproject_cinemabooking.repository.RoomRepository;
import finalproject_cinemabooking.repository.ShowtimeRepository;
import finalproject_cinemabooking.repository.TicketRepository;
import finalproject_cinemabooking.service.ShowtimeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ShowtimeServiceImpl implements ShowtimeService {

    @Autowired
    private ShowtimeRepository showtimeRepository;
    @Autowired
    private MovieRepository movieRepository;
    @Autowired
    private RoomRepository roomRepository;
    @Autowired
    private TicketRepository ticketRepository;

    @Override
    public Showtime createShowtime(ShowtimeCreateDTO dto) {
        Movie movie = movieRepository.findById(dto.getMovieId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim!"));
        Room room = roomRepository.findById(dto.getRoomId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phòng chiếu!"));

        LocalDateTime startTime = dto.getStartTime();
        if (startTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Không được tạo xuất chiếu với thời gian trong quá khứ.");
        }
        LocalTime time = startTime.toLocalTime();
        if (time.isAfter(LocalTime.of(0, 0)) && time.isBefore(LocalTime.of(7, 0))) {
            throw new RuntimeException("Rạp đang đóng cửa (từ 12AM đến 7AM), vui lòng tạo thời gian khác.");
        }

        LocalDateTime endTime = startTime.plusMinutes(movie.getDuration()).plusMinutes(15);

        List<Showtime> conflicts = showtimeRepository.findConflictingShowtimes(room.getId(), startTime, endTime);
        if (!conflicts.isEmpty()) {
            throw new RuntimeException("Phòng chiếu đã bị trùng lịch trong khoảng thời gian này!");
        }

        Showtime showtime = new Showtime();
        showtime.setMovie(movie);
        showtime.setRoom(room);
        showtime.setStartTime(startTime);
        showtime.setEndTime(endTime);
        showtime.setStatus(Showtime.ShowtimeStatus.SCHEDULED);
        return showtimeRepository.save(showtime);
    }

    @Override
    public List<Showtime> getAllShowtimes() {
        return showtimeRepository.findByStatusNotOrderByStartTimeAsc(Showtime.ShowtimeStatus.DELETED);
    }

    @Override
    public List<AdminShowtimeDTO> getAllShowtimesForAdmin() {
        updateShowtimeStatus();
        List<Showtime> showtimes = showtimeRepository.findAllByOrderByStartTimeAsc();
        return showtimes.stream().map(showtime -> {
            long soldTickets = ticketRepository.countConfirmedTicketsByShowtime(showtime.getId());
            boolean isDeletable = soldTickets == 0;
            return new AdminShowtimeDTO(showtime, isDeletable, soldTickets);
        }).collect(Collectors.toList());
    }

    @Override
    public void deleteShowtime(Long id) {
        Showtime showtime = showtimeRepository.findById(id)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy suất chiếu với id: " + id));

        long soldTickets = ticketRepository.countConfirmedTicketsByShowtime(id);
        if (soldTickets > 0) {
            throw new IllegalStateException("Không thể xóa suất chiếu đã có vé đặt. Vui lòng ẩn nó thay thế.");
        }

        showtimeRepository.deleteById(id);
    }

    @Override
    public Showtime toggleShowtimeVisibility(Long id) {
        Showtime showtime = showtimeRepository.findByIdWithMovieAndRoom(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy suất chiếu!"));
        if (showtime.getStatus() == Showtime.ShowtimeStatus.DELETED) {
            showtime.setStatus(resolveVisibleStatus(showtime));
        } else {
            showtime.setStatus(Showtime.ShowtimeStatus.DELETED);
        }
        return showtimeRepository.save(showtime);
    }

    @Override
    public void updateShowtimeStatus() {
        List<Showtime> showtimes = showtimeRepository.findByStatusNotOrderByStartTimeAsc(Showtime.ShowtimeStatus.DELETED);
        for (Showtime showtime : showtimes) {
            showtime.setStatus(resolveVisibleStatus(showtime));
            showtimeRepository.save(showtime);
        }
    }

    private Showtime.ShowtimeStatus resolveVisibleStatus(Showtime showtime) {
        LocalDateTime now = LocalDateTime.now();
        if (now.isAfter(showtime.getStartTime())) {
            return Showtime.ShowtimeStatus.ENDED;
        }
        long soldTickets = ticketRepository.countConfirmedTicketsByShowtime(showtime.getId());
        if (soldTickets >= showtime.getRoom().getCapacity()) {
            return Showtime.ShowtimeStatus.SOLD_OUT;
        }
        return Showtime.ShowtimeStatus.SCHEDULED;
    }
}
