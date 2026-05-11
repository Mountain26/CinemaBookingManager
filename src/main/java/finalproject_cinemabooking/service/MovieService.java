package finalproject_cinemabooking.service;

import finalproject_cinemabooking.model.entity.Movie;
import java.util.List;

public interface MovieService {
    Movie createMovie(Movie movie);
    Movie updateMovie(Long id, Movie movie);
    void deleteMovie(Long id);
    List<Movie> getAllMovies();
    Movie getMovieById(Long id);
}

