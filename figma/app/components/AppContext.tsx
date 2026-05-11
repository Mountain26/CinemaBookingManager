import { createContext, useContext, useState, ReactNode } from 'react';
import { Movie, Showtime, Room, Seat, FoodCombo, Booking, User } from './types';
import { mockMovies, mockShowtimes, mockRooms, mockCombos } from './mockData';

interface AppContextType {
  user: User | null;
  setUser: (user: User | null) => void;
  currentView: string;
  setCurrentView: (view: string) => void;
  movies: Movie[];
  showtimes: Showtime[];
  rooms: Room[];
  combos: FoodCombo[];
  bookings: Booking[];
  addBooking: (booking: Booking) => void;
  updateBooking: (id: string, updates: Partial<Booking>) => void;
  selectedMovie: Movie | null;
  setSelectedMovie: (movie: Movie | null) => void;
  selectedShowtime: Showtime | null;
  setSelectedShowtime: (showtime: Showtime | null) => void;
  selectedSeats: Seat[];
  setSelectedSeats: (seats: Seat[]) => void;
  selectedCombos: { id: string; quantity: number }[];
  setSelectedCombos: (combos: { id: string; quantity: number }[]) => void;
}

const AppContext = createContext<AppContextType | undefined>(undefined);

export function AppProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [currentView, setCurrentView] = useState('auth');
  const [movies] = useState<Movie[]>(mockMovies);
  const [showtimes] = useState<Showtime[]>(mockShowtimes);
  const [rooms] = useState<Room[]>(mockRooms);
  const [combos] = useState<FoodCombo[]>(mockCombos);
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [selectedMovie, setSelectedMovie] = useState<Movie | null>(null);
  const [selectedShowtime, setSelectedShowtime] = useState<Showtime | null>(null);
  const [selectedSeats, setSelectedSeats] = useState<Seat[]>([]);
  const [selectedCombos, setSelectedCombos] = useState<{ id: string; quantity: number }[]>([]);

  const addBooking = (booking: Booking) => {
    setBookings([...bookings, booking]);
  };

  const updateBooking = (id: string, updates: Partial<Booking>) => {
    setBookings(bookings.map(b => b.id === id ? { ...b, ...updates } : b));
  };

  return (
    <AppContext.Provider
      value={{
        user,
        setUser,
        currentView,
        setCurrentView,
        movies,
        showtimes,
        rooms,
        combos,
        bookings,
        addBooking,
        updateBooking,
        selectedMovie,
        setSelectedMovie,
        selectedShowtime,
        setSelectedShowtime,
        selectedSeats,
        setSelectedSeats,
        selectedCombos,
        setSelectedCombos,
      }}
    >
      {children}
    </AppContext.Provider>
  );
}

export function useApp() {
  const context = useContext(AppContext);
  if (!context) throw new Error('useApp must be used within AppProvider');
  return context;
}
