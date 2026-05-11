package finalproject_cinemabooking.controller;

import finalproject_cinemabooking.model.entity.User;
import finalproject_cinemabooking.repository.RoomRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/admin/rooms")
public class AdminRoomController {
    private final RoomRepository roomRepository;

    public AdminRoomController(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {
        User.Role role = (User.Role) session.getAttribute("ROLE");
        if (role != User.Role.ADMIN) return ResponseEntity.status(403).body("Không có quyền truy cập.");
        return ResponseEntity.ok(roomRepository.findAll());
    }
}
