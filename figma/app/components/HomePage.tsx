import { useState } from 'react';
import { useApp } from './AppContext';
import { Star, Clock, Play, Calendar } from 'lucide-react';
import { Movie } from './types';

export function HomePage() {
  const { movies, setSelectedMovie, setCurrentView } = useApp();
  const [selectedTrailer, setSelectedTrailer] = useState<Movie | null>(null);
  const [currentSlide, setCurrentSlide] = useState(0);

  const featuredMovies = movies.filter(m => m.status === 'now-showing').slice(0, 3);
  const nowShowing = movies.filter(m => m.status === 'now-showing');
  const comingSoon = movies.filter(m => m.status === 'coming-soon');

  const handleBookNow = (movie: Movie) => {
    setSelectedMovie(movie);
    setCurrentView('showtime');
  };

  return (
    <div className="min-h-screen bg-background pb-20">
      <div className="relative h-[70vh] overflow-hidden">
        {featuredMovies.map((movie, index) => (
          <div
            key={movie.id}
            className={`absolute inset-0 transition-opacity duration-1000 ${
              index === currentSlide ? 'opacity-100' : 'opacity-0'
            }`}
          >
            <div
              className="absolute inset-0 bg-cover bg-center"
              style={{ backgroundImage: `url(${movie.poster})` }}
            />
            <div className="absolute inset-0 bg-gradient-to-t from-background via-background/60 to-transparent" />
            <div className="absolute bottom-0 left-0 right-0 p-8 md:p-16">
              <div className="max-w-2xl">
                <h1 className="text-5xl md:text-6xl mb-4">{movie.title}</h1>
                <p className="text-lg mb-6 text-foreground/80">{movie.description}</p>
                <div className="flex flex-wrap gap-4 mb-6">
                  <span className="px-3 py-1 bg-secondary rounded text-sm">{movie.genre}</span>
                  <span className="flex items-center gap-1">
                    <Clock className="w-4 h-4" />
                    {movie.duration} min
                  </span>
                  <span className="flex items-center gap-1">
                    <Star className="w-4 h-4 text-primary fill-primary" />
                    {movie.rating}/5
                  </span>
                </div>
                <div className="flex gap-4">
                  <button
                    onClick={() => handleBookNow(movie)}
                    className="px-8 py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors"
                  >
                    Book Now
                  </button>
                  <button
                    onClick={() => setSelectedTrailer(movie)}
                    className="px-8 py-3 bg-secondary text-foreground rounded-lg hover:bg-secondary/80 transition-colors flex items-center gap-2"
                  >
                    <Play className="w-5 h-5" />
                    Watch Trailer
                  </button>
                </div>
              </div>
            </div>
          </div>
        ))}
        <div className="absolute bottom-8 right-8 flex gap-2">
          {featuredMovies.map((_, index) => (
            <button
              key={index}
              onClick={() => setCurrentSlide(index)}
              className={`w-3 h-3 rounded-full transition-colors ${
                index === currentSlide ? 'bg-primary' : 'bg-white/30'
              }`}
            />
          ))}
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 md:px-8 py-12 space-y-12">
        <section>
          <h2 className="text-3xl mb-6">Now Showing</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
            {nowShowing.map(movie => (
              <MovieCard key={movie.id} movie={movie} onBook={handleBookNow} />
            ))}
          </div>
        </section>

        <section>
          <h2 className="text-3xl mb-6">Coming Soon</h2>
          <div className="grid grid-cols-2 md:grid-cols-3 lg:grid-cols-4 xl:grid-cols-5 gap-6">
            {comingSoon.map(movie => (
              <MovieCard key={movie.id} movie={movie} onBook={handleBookNow} comingSoon />
            ))}
          </div>
        </section>
      </div>

      {selectedTrailer && (
        <div
          className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4"
          onClick={() => setSelectedTrailer(null)}
        >
          <div className="bg-card rounded-lg p-6 max-w-2xl w-full" onClick={e => e.stopPropagation()}>
            <h3 className="text-2xl mb-4">{selectedTrailer.title} - Trailer</h3>
            <div className="aspect-video bg-secondary rounded-lg flex items-center justify-center">
              <Play className="w-16 h-16 text-primary" />
            </div>
            <button
              onClick={() => setSelectedTrailer(null)}
              className="mt-4 w-full py-2 bg-secondary rounded hover:bg-secondary/80"
            >
              Close
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

function MovieCard({ movie, onBook, comingSoon }: { movie: Movie; onBook: (movie: Movie) => void; comingSoon?: boolean }) {
  return (
    <div className="group cursor-pointer">
      <div className="relative aspect-[2/3] rounded-lg overflow-hidden mb-3">
        <img src={movie.poster} alt={movie.title} className="w-full h-full object-cover" />
        {movie.soldOut && (
          <div className="absolute inset-0 bg-destructive/90 flex items-center justify-center">
            <span className="text-2xl rotate-[-15deg] border-4 border-white px-6 py-2">SOLD OUT</span>
          </div>
        )}
        <div className="absolute inset-0 bg-gradient-to-t from-black/80 via-transparent to-transparent opacity-0 group-hover:opacity-100 transition-opacity">
          <div className="absolute bottom-0 left-0 right-0 p-4">
            {!movie.soldOut && !comingSoon && (
              <button
                onClick={() => onBook(movie)}
                className="w-full py-2 bg-primary text-primary-foreground rounded hover:bg-primary/90"
              >
                Book Now
              </button>
            )}
            {comingSoon && (
              <div className="flex items-center justify-center gap-2 text-primary">
                <Calendar className="w-5 h-5" />
                <span>Coming Soon</span>
              </div>
            )}
          </div>
        </div>
      </div>
      <h3 className="mb-1 line-clamp-1">{movie.title}</h3>
      <p className="text-sm text-muted-foreground mb-1">{movie.genre}</p>
      <div className="flex items-center gap-2 text-sm">
        <span className="flex items-center gap-1">
          <Clock className="w-3 h-3" />
          {movie.duration}m
        </span>
        <span className="flex items-center gap-1">
          <Star className="w-3 h-3 text-primary fill-primary" />
          {movie.rating}
        </span>
      </div>
    </div>
  );
}
