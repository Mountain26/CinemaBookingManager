package finalproject_cinemabooking.service.impl;

import finalproject_cinemabooking.model.entity.Movie;
import finalproject_cinemabooking.repository.MovieRepository;
import finalproject_cinemabooking.service.MovieService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MovieServiceImpl implements MovieService {

    @Autowired
    private MovieRepository movieRepository;

    @Override
    public Movie createMovie(Movie movie) {
        return movieRepository.save(movie);
    }

    @Override
    public Movie updateMovie(Long id, Movie movie) {
        Movie existing = getMovieById(id);
        existing.setTitle(movie.getTitle());
        existing.setDuration(movie.getDuration());
        existing.setGenre(movie.getGenre());
        if (movie.getPosterUrl() != null && !movie.getPosterUrl().isBlank()) {
            existing.setPosterUrl(movie.getPosterUrl());
        }
        existing.setDescription(movie.getDescription());
        return movieRepository.save(existing);
    }

    @Override
    public void deleteMovie(Long id) {
        movieRepository.deleteById(id);
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public Movie getMovieById(Long id) {
        return movieRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy phim!"));
    }
}

