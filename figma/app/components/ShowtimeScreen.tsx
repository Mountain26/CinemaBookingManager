import { useState } from 'react';
import { useApp } from './AppContext';
import { ChevronLeft, Play, Clock, Star } from 'lucide-react';
import { format, addDays, parseISO } from 'date-fns';

export function ShowtimeScreen() {
  const { selectedMovie, setCurrentView, showtimes, rooms, setSelectedShowtime } = useApp();
  const [selectedDate, setSelectedDate] = useState('2026-05-07');

  if (!selectedMovie) return null;

  const dates = Array.from({ length: 7 }, (_, i) => {
    const date = addDays(new Date('2026-05-07'), i);
    return format(date, 'yyyy-MM-dd');
  });

  const movieShowtimes = showtimes.filter(
    s => s.movieId === selectedMovie.id && s.date === selectedDate
  );

  const showtimesByRoom = rooms.map(room => ({
    room,
    times: movieShowtimes.filter(s => s.roomId === room.id),
  })).filter(r => r.times.length > 0);

  const handleSelectShowtime = (showtime: any) => {
    if (showtime.availableSeats === 0 || showtime.isPast) return;
    setSelectedShowtime(showtime);
    setCurrentView('seats');
  };

  return (
    <div className="min-h-screen bg-background pb-20">
      <div className="relative h-80 bg-cover bg-center" style={{ backgroundImage: `url(${selectedMovie.poster})` }}>
        <div className="absolute inset-0 bg-gradient-to-t from-background via-background/60 to-background/30" />
        <button
          onClick={() => setCurrentView('home')}
          className="absolute top-4 left-4 p-2 bg-black/50 rounded-full hover:bg-black/70"
        >
          <ChevronLeft className="w-6 h-6" />
        </button>
      </div>

      <div className="max-w-7xl mx-auto px-4 md:px-8 -mt-20 relative z-10">
        <div className="bg-card rounded-lg p-6 shadow-lg">
          <div className="flex flex-col md:flex-row gap-6">
            <div className="md:w-1/3">
              <img src={selectedMovie.poster} alt={selectedMovie.title} className="w-full rounded-lg" />
            </div>
            <div className="md:w-2/3">
              <h1 className="text-3xl mb-4">{selectedMovie.title}</h1>
              <div className="flex flex-wrap gap-4 mb-4">
                <span className="px-3 py-1 bg-secondary rounded text-sm">{selectedMovie.genre}</span>
                <span className="flex items-center gap-1">
                  <Clock className="w-4 h-4" />
                  {selectedMovie.duration} min
                </span>
                <span className="flex items-center gap-1">
                  <Star className="w-4 h-4 text-primary fill-primary" />
                  {selectedMovie.rating}/5
                </span>
              </div>
              <p className="text-muted-foreground mb-4">{selectedMovie.description}</p>
              <div className="mb-4">
                <h3 className="mb-2">Cast</h3>
                <p className="text-muted-foreground">{selectedMovie.cast.join(', ')}</p>
              </div>
              <button className="flex items-center gap-2 text-primary hover:underline">
                <Play className="w-5 h-5" />
                Watch Trailer
              </button>
            </div>
          </div>
        </div>

        <div className="mt-8">
          <h2 className="text-2xl mb-4">Select Date</h2>
          <div className="flex gap-2 overflow-x-auto pb-2">
            {dates.map(date => {
              const dateObj = parseISO(date);
              const isSelected = date === selectedDate;
              return (
                <button
                  key={date}
                  onClick={() => setSelectedDate(date)}
                  className={`flex-shrink-0 px-6 py-3 rounded-lg border transition-colors ${
                    isSelected
                      ? 'bg-primary text-primary-foreground border-primary'
                      : 'bg-card border-border hover:border-primary'
                  }`}
                >
                  <div className="text-sm">{format(dateObj, 'EEE')}</div>
                  <div>{format(dateObj, 'MMM d')}</div>
                </button>
              );
            })}
          </div>
        </div>

        <div className="mt-8 space-y-6">
          {showtimesByRoom.length === 0 ? (
            <div className="text-center py-12 text-muted-foreground">
              No showtimes available for this date
            </div>
          ) : (
            showtimesByRoom.map(({ room, times }) => (
              <div key={room.id} className="bg-card rounded-lg p-6">
                <h3 className="text-xl mb-4">{room.name}</h3>
                <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-3">
                  {times.map(showtime => {
                    const isDisabled = showtime.availableSeats === 0 || showtime.isPast;
                    return (
                      <button
                        key={showtime.id}
                        onClick={() => handleSelectShowtime(showtime)}
                        disabled={isDisabled}
                        className={`py-3 px-4 rounded-lg border transition-colors ${
                          isDisabled
                            ? 'bg-muted text-muted-foreground border-border cursor-not-allowed opacity-50'
                            : 'bg-background border-border hover:border-primary hover:bg-primary/10'
                        }`}
                      >
                        <div>{showtime.time}</div>
                        <div className="text-xs text-muted-foreground mt-1">
                          {showtime.availableSeats} seats
                        </div>
                      </button>
                    );
                  })}
                </div>
              </div>
            ))
          )}
        </div>
      </div>
    </div>
  );
}
