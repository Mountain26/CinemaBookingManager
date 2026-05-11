package finalproject_cinemabooking.controller;

import finalproject_cinemabooking.model.dto.AdminShowtimeDTO;
import finalproject_cinemabooking.model.dto.ShowtimeCreateDTO;
import finalproject_cinemabooking.service.ShowtimeService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/showtimes")
public class AdminShowtimeController {

    private final ShowtimeService showtimeService;

    public AdminShowtimeController(ShowtimeService showtimeService) {
        this.showtimeService = showtimeService;
    }

    @GetMapping("/all")
    public ResponseEntity<List<AdminShowtimeDTO>> getAllShowtimesForAdmin() {
        return ResponseEntity.ok(showtimeService.getAllShowtimesForAdmin());
    }

    @PostMapping
    public ResponseEntity<?> createShowtime(@RequestBody ShowtimeCreateDTO dto) {
        try {
            return ResponseEntity.ok(showtimeService.createShowtime(dto));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PatchMapping("/{id}/visibility")
    public ResponseEntity<?> toggleShowtimeVisibility(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(showtimeService.toggleShowtimeVisibility(id));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteShowtime(@PathVariable Long id) {
        try {
            showtimeService.deleteShowtime(id);
            return ResponseEntity.ok().body("Showtime deleted successfully.");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("An error occurred while deleting the showtime.");
        }
    }
}
