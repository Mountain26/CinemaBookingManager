package finalproject_cinemabooking.controller;

import finalproject_cinemabooking.model.entity.Movie;
import finalproject_cinemabooking.model.entity.User;
import finalproject_cinemabooking.service.MovieService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/movies")
public class AdminMovieController {
    private final MovieService movieService;
    private final String uploadPath;

    public AdminMovieController(MovieService movieService, @Value("${upload.path}") String uploadPath) {
        this.movieService = movieService;
        this.uploadPath = uploadPath;
    }

    private boolean isAdmin(HttpSession session) {
        User.Role role = (User.Role) session.getAttribute("ROLE");
        return role == User.Role.ADMIN;
    }

    @GetMapping
    public ResponseEntity<?> getAll(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("Không có quyền truy cập.");
        return ResponseEntity.ok(movieService.getAllMovies());
    }

    @GetMapping("/genres")
    public ResponseEntity<?> getGenres(HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("Không có quyền truy cập.");
        List<String> genres = movieService.getAllMovies().stream()
                .map(Movie::getGenre)
                .filter(g -> g != null && !g.isBlank())
                .distinct()
                .toList();
        return ResponseEntity.ok(genres);
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestParam("title") String title,
                                    @RequestParam("genre") String genre,
                                    @RequestParam("duration") Integer duration,
                                    @RequestParam(value = "description", required = false) String description,
                                    @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
                                     HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("Không có quyền truy cập.");
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setDuration(duration);
        movie.setDescription(description);
        try {
            String posterUrl = savePosterFile(posterFile);
            if (posterUrl != null) {
                movie.setPosterUrl(posterUrl);
            }
        } catch (IOException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok(movieService.createMovie(movie));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestParam("title") String title,
                                    @RequestParam("genre") String genre,
                                    @RequestParam("duration") Integer duration,
                                    @RequestParam(value = "description", required = false) String description,
                                    @RequestParam(value = "posterFile", required = false) MultipartFile posterFile,
                                    HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("Không có quyền truy cập.");
        Movie movie = new Movie();
        movie.setTitle(title);
        movie.setGenre(genre);
        movie.setDuration(duration);
        movie.setDescription(description);
        try {
            String posterUrl = savePosterFile(posterFile);
            if (posterUrl != null) {
                movie.setPosterUrl(posterUrl);
            }
        } catch (IOException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
        return ResponseEntity.ok(movieService.updateMovie(id, movie));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id, HttpSession session) {
        if (!isAdmin(session)) return ResponseEntity.status(403).body("Không có quyền truy cập.");
        movieService.deleteMovie(id);
        return ResponseEntity.ok("Xóa phim thành công.");
    }

    private String savePosterFile(MultipartFile posterFile) throws IOException {
        if (posterFile == null || posterFile.isEmpty()) {
            return null;
        }
        String contentType = posterFile.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IOException("File upload không hợp lệ (chỉ chấp nhận hình ảnh).");
        }
        String originalName = posterFile.getOriginalFilename();
        String extension = "";
        if (originalName != null && originalName.contains(".")) {
            extension = originalName.substring(originalName.lastIndexOf('.'));
        }
        String fileName = UUID.randomUUID() + extension;
        Path uploadDir = Paths.get(uploadPath);
        Files.createDirectories(uploadDir);
        Path target = uploadDir.resolve(fileName);
        posterFile.transferTo(target.toFile());
        return "/uploads/" + fileName;
    }
}
