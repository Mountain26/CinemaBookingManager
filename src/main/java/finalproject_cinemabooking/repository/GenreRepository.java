package finalproject_cinemabooking.repository;

import finalproject_cinemabooking.model.entity.Genre;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GenreRepository extends JpaRepository<Genre, Long> {
    Optional<Genre> findByNameIgnoreCase(String name);
    List<Genre> findAllByOrderByNameAsc();
}
