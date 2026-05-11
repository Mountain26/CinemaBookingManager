package finalproject_cinemabooking.controller;

import finalproject_cinemabooking.model.entity.Genre;
import finalproject_cinemabooking.model.entity.User;
import finalproject_cinemabooking.repository.GenreRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/genres")
public class AdminGenreController {
    private final GenreRepository genreRepository;

    public AdminGenreController(GenreRepository genreRepository) {
        this.genreRepository = genreRepository;
    }

    private boolean isAdmin(HttpSession session) {
        User.Role role = (User.Role) session.getAttribute("ROLE");
        return role == User.Role.ADMIN;
    }

    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("Không có quyền truy cập.");
        return ResponseEntity.ok(genreRepository.findAllByOrderByNameAsc());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody Genre genre, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("Không có quyền truy cập.");
        String name = genre.getName() == null ? "" : genre.getName().trim();
        if (name.isEmpty()) return ResponseEntity.badRequest().body("Tên thể loại không được bỏ trống.");
        if (genreRepository.findByNameIgnoreCase(name).isPresent()) return ResponseEntity.badRequest().body("Thể loại đã tồn tại.");
        Genre saved = new Genre();
        saved.setName(name);
        return ResponseEntity.ok(genreRepository.save(saved));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Genre genre, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("Không có quyền truy cập.");
        Genre existing = genreRepository.findById(id).orElse(null);
        if (existing == null) return ResponseEntity.badRequest().body("Không tìm thấy thể loại.");
        String name = genre.getName() == null ? "" : genre.getName().trim();
        if (name.isEmpty()) return ResponseEntity.badRequest().body("Tên thể loại không được bỏ trống.");
        Genre dup = genreRepository.findByNameIgnoreCase(name).orElse(null);
        if (dup != null && !dup.getId().equals(id)) return ResponseEntity.badRequest().body("Thể loại đã tồn tại.");
        existing.setName(name);
        return ResponseEntity.ok(genreRepository.save(existing));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("Không có quyền truy cập.");
        if (!genreRepository.existsById(id)) return ResponseEntity.badRequest().body("Không tìm thấy thể loại.");
        genreRepository.deleteById(id);
        return ResponseEntity.ok("Xóa thể loại thành công.");
    }
}
