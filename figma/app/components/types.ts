export interface Movie {
  id: string;
  title: string;
  poster: string;
  genre: string;
  duration: number;
  rating: number;
  description: string;
  cast: string[];
  trailer: string;
  status: 'now-showing' | 'coming-soon';
  soldOut?: boolean;
}

export interface Showtime {
  id: string;
  movieId: string;
  roomId: string;
  date: string;
  time: string;
  availableSeats: number;
  totalSeats: number;
  isPast?: boolean;
}

export interface Room {
  id: string;
  name: string;
  rows: number;
  seatsPerRow: number;
  vipRows: number[];
}

export interface Seat {
  row: string;
  number: number;
  isVip: boolean;
  isReserved: boolean;
  isSelected: boolean;
}

export interface FoodCombo {
  id: string;
  name: string;
  image: string;
  price: number;
  description: string;
}

export interface Booking {
  id: string;
  movieId: string;
  movieTitle: string;
  moviePoster: string;
  showtime: string;
  date: string;
  room: string;
  seats: string[];
  combos: { id: string; name: string; quantity: number; price: number }[];
  total: number;
  status: 'valid' | 'used' | 'cancelled';
  qrCode: string;
}

export interface User {
  id: string;
  name: string;
  email: string;
  phone: string;
  role: 'customer' | 'admin' | 'staff';
}
