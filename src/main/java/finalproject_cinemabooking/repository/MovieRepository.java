package finalproject_cinemabooking.repository;

import finalproject_cinemabooking.model.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    List<Movie> findByTitleContainingIgnoreCase(String keyword);
    List<Movie> findByGenre(String genre);
    List<Movie> findByGenreAndTitleContainingIgnoreCase(String genre, String keyword);
}
