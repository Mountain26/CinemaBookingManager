import { useState, useEffect } from 'react';
import { useApp } from './AppContext';
import { ChevronLeft, Monitor } from 'lucide-react';
import { Seat } from './types';

export function SeatsScreen() {
  const { selectedMovie, selectedShowtime, setCurrentView, rooms, selectedSeats, setSelectedSeats } = useApp();
  const [seats, setSeats] = useState<Seat[]>([]);

  const room = rooms.find(r => r.id === selectedShowtime?.roomId);

  useEffect(() => {
    if (!room) return;

    const generatedSeats: Seat[] = [];
    for (let row = 0; row < room.rows; row++) {
      for (let seat = 1; seat <= room.seatsPerRow; seat++) {
        const rowLetter = String.fromCharCode(65 + row);
        const isReserved = Math.random() > 0.7;
        generatedSeats.push({
          row: rowLetter,
          number: seat,
          isVip: room.vipRows.includes(row),
          isReserved,
          isSelected: false,
        });
      }
    }
    setSeats(generatedSeats);
  }, [room]);

  if (!selectedMovie || !selectedShowtime || !room) return null;

  const handleSeatClick = (seat: Seat) => {
    if (seat.isReserved) return;

    const seatId = `${seat.row}${seat.number}`;
    const isCurrentlySelected = selectedSeats.some(s => `${s.row}${s.number}` === seatId);

    if (isCurrentlySelected) {
      setSelectedSeats(selectedSeats.filter(s => `${s.row}${s.number}` !== seatId));
    } else {
      setSelectedSeats([...selectedSeats, seat]);
    }
  };

  const totalPrice = selectedSeats.reduce((sum, seat) => {
    return sum + (seat.isVip ? 15 : 10);
  }, 0);

  const handleNext = () => {
    if (selectedSeats.length > 0) {
      setCurrentView('checkout');
    }
  };

  return (
    <div className="min-h-screen bg-background pb-32">
      <div className="bg-card border-b border-border sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 py-4 flex items-center gap-4">
          <button onClick={() => setCurrentView('showtime')} className="p-2 hover:bg-secondary rounded-lg">
            <ChevronLeft className="w-6 h-6" />
          </button>
          <div>
            <h2>{selectedMovie.title}</h2>
            <p className="text-sm text-muted-foreground">
              {selectedShowtime.date} • {selectedShowtime.time} • {room.name}
            </p>
          </div>
        </div>
      </div>

      <div className="max-w-4xl mx-auto px-4 py-8">
        <div className="mb-8 flex items-center justify-center">
          <div className="w-full max-w-2xl">
            <div className="bg-gradient-to-b from-primary/20 to-transparent h-2 rounded-t-full mb-2" />
            <div className="flex items-center justify-center gap-2 text-muted-foreground">
              <Monitor className="w-5 h-5" />
              <span>SCREEN</span>
            </div>
          </div>
        </div>

        <div className="mb-8 flex justify-center gap-6 flex-wrap">
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 rounded bg-background border-2 border-border" />
            <span className="text-sm">Available</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 rounded bg-primary" />
            <span className="text-sm">Selected</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 rounded bg-muted" />
            <span className="text-sm">Reserved</span>
          </div>
          <div className="flex items-center gap-2">
            <div className="w-6 h-6 rounded bg-primary/30 border-2 border-primary" />
            <span className="text-sm">VIP</span>
          </div>
        </div>

        <div className="space-y-2">
          {Array.from({ length: room.rows }, (_, rowIndex) => {
            const rowLetter = String.fromCharCode(65 + rowIndex);
            const rowSeats = seats.filter(s => s.row === rowLetter);
            const isVipRow = room.vipRows.includes(rowIndex);

            return (
              <div key={rowLetter} className="flex items-center justify-center gap-2">
                <div className="w-8 text-center text-sm text-muted-foreground">{rowLetter}</div>
                <div className="flex gap-2">
                  {rowSeats.map(seat => {
                    const seatId = `${seat.row}${seat.number}`;
                    const isSelected = selectedSeats.some(s => `${s.row}${s.number}` === seatId);

                    return (
                      <button
                        key={seatId}
                        onClick={() => handleSeatClick(seat)}
                        disabled={seat.isReserved}
                        className={`w-8 h-8 rounded text-xs transition-all ${
                          seat.isReserved
                            ? 'bg-muted cursor-not-allowed'
                            : isSelected
                            ? 'bg-primary text-primary-foreground scale-110'
                            : isVipRow
                            ? 'bg-primary/30 border-2 border-primary hover:scale-110'
                            : 'bg-background border-2 border-border hover:border-primary hover:scale-110'
                        }`}
                      >
                        {seat.number}
                      </button>
                    );
                  })}
                </div>
                <div className="w-8 text-center text-sm text-muted-foreground">{rowLetter}</div>
              </div>
            );
          })}
        </div>
      </div>

      {selectedSeats.length > 0 && (
        <div className="fixed bottom-0 left-0 right-0 bg-card border-t border-border p-4 shadow-lg">
          <div className="max-w-7xl mx-auto flex items-center justify-between">
            <div>
              <div className="text-sm text-muted-foreground">Selected Seats</div>
              <div className="flex gap-1 flex-wrap">
                {selectedSeats.map(s => `${s.row}${s.number}`).join(', ')}
              </div>
              <div className="text-xl text-primary mt-1">${totalPrice.toFixed(2)}</div>
            </div>
            <button
              onClick={handleNext}
              className="px-8 py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90"
            >
              Next
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
