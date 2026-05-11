package finalproject_cinemabooking.controller;

import finalproject_cinemabooking.model.entity.Room;
import finalproject_cinemabooking.model.entity.Seat;
import finalproject_cinemabooking.model.entity.User;
import finalproject_cinemabooking.repository.RoomRepository;
import finalproject_cinemabooking.repository.SeatRepository;
import finalproject_cinemabooking.repository.ShowtimeRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api/admin/rooms")
public class AdminRoomController {
    private final RoomRepository roomRepository;
    private final ShowtimeRepository showtimeRepository;
    private final SeatRepository seatRepository;

    public AdminRoomController(RoomRepository roomRepository, ShowtimeRepository showtimeRepository, SeatRepository seatRepository) {
        this.roomRepository = roomRepository;
        this.showtimeRepository = showtimeRepository;
        this.seatRepository = seatRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {
        if (isAdmin(session)) {
            return ResponseEntity.ok(roomRepository.findAll());
        }
        return ResponseEntity.status(403).body("Không có quyền truy cập.");
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Room room, HttpSession session) {
        if (isAdmin(session)) {
            String error = validate(room, null);
            if (error != null) return ResponseEntity.badRequest().body(error);

            room.setId(null);
            Room savedRoom = roomRepository.save(room);
            createSeatsForRoom(savedRoom);
            return ResponseEntity.ok(savedRoom);
        }
        return ResponseEntity.status(403).body("Không có quyền truy cập.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Room room, HttpSession session) {
        if (isAdmin(session)) {
            Room existing = roomRepository.findById(id).orElse(null);
            if (existing == null) return ResponseEntity.badRequest().body("Không tìm thấy phòng chiếu.");

            String error = validate(room, id);
            if (error != null) return ResponseEntity.badRequest().body(error);

            existing.setName(room.getName().trim());
            existing.setCapacity(room.getCapacity());
            return ResponseEntity.ok(roomRepository.save(existing));
        }
        return ResponseEntity.status(403).body("Không có quyền truy cập.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        if (isAdmin(session)) {
            Room existing = roomRepository.findById(id).orElse(null);
            if (existing == null) return ResponseEntity.badRequest().body("Không tìm thấy phòng chiếu.");

            boolean hasSeats = !seatRepository.findByRoomId(id).isEmpty();
            boolean hasShowtimes = showtimeRepository.existsByRoomId(id);
            if (hasSeats || hasShowtimes) {
                return ResponseEntity.badRequest().body("Không thể xóa phòng đang được sử dụng.");
            }

            roomRepository.delete(existing);
            return ResponseEntity.ok("Xóa phòng thành công.");
        }
        return ResponseEntity.status(403).body("Không có quyền truy cập.");
    }

    private boolean isAdmin(HttpSession session) {
        User.Role role = (User.Role) session.getAttribute("ROLE");
        return role == User.Role.ADMIN;
    }

    private String validate(Room room, Long currentId) {
        if (room == null) return "Dữ liệu phòng không hợp lệ.";

        String name = room.getName() == null ? "" : room.getName().trim();
        if (name.isEmpty()) return "Tên phòng không được để trống.";

        Integer capacity = room.getCapacity();
        if (capacity == null || capacity <= 0) return "Sức chứa phải lớn hơn 0.";

        boolean duplicatedName = roomRepository.findAll().stream()
                .anyMatch(item -> item.getName() != null
                        && item.getName().trim().equalsIgnoreCase(name)
                        && !Objects.equals(item.getId(), currentId));
        if (duplicatedName) return "Tên phòng đã tồn tại.";

        room.setName(name);
        room.setCapacity(capacity);
        return null;
    }

    private void createSeatsForRoom(Room room) {
        if (room == null || room.getId() == null || room.getCapacity() == null || room.getCapacity() <= 0) {
            return;
        }

        int capacity = room.getCapacity();
        int seatsPerRow = 10; // 10 ghế per hàng
        int rows = (capacity + seatsPerRow - 1) / seatsPerRow; // làm tròn lên

        List<Seat> seats = new ArrayList<>();
        int seatCount = 0;

        for (int row = 0; row < rows && seatCount < capacity; row++) {
            char rowLetter = (char) ('A' + row); // A, B, C, ...
            for (int col = 1; col <= seatsPerRow && seatCount < capacity; col++) {
                Seat seat = new Seat();
                seat.setRoom(room);
                seat.setSeatName(rowLetter + String.valueOf(col)); // A1, A2, ..., B1, B2, ...
                seat.setSeatNumber(seatCount + 1); // Số thứ tự từ 1 đến capacity
                seat.setStatus(Seat.SeatStatus.AVAILABLE);
                seats.add(seat);
                seatCount++;
            }
        }

        if (!seats.isEmpty()) {
            seatRepository.saveAll(seats);
        }
    }
}
