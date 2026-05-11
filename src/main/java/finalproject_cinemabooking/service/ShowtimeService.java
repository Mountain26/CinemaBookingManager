package finalproject_cinemabooking.service;

import finalproject_cinemabooking.model.dto.AdminShowtimeDTO;
import finalproject_cinemabooking.model.dto.ShowtimeCreateDTO;
import finalproject_cinemabooking.model.entity.Showtime;

import java.util.List;

public interface ShowtimeService {
    Showtime createShowtime(ShowtimeCreateDTO dto);
    List<Showtime> getAllShowtimes();
    List<AdminShowtimeDTO> getAllShowtimesForAdmin();
    void deleteShowtime(Long id);
    Showtime toggleShowtimeVisibility(Long id);
    void updateShowtimeStatus();
}
