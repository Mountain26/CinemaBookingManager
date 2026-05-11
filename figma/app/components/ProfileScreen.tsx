import { useState } from 'react';
import { useApp } from './AppContext';
import { User, Ticket, ChevronLeft, QrCode } from 'lucide-react';
import { Booking } from './types';

export function ProfileScreen() {
  const { user, bookings, updateBooking, setCurrentView } = useApp();
  const [activeTab, setActiveTab] = useState<'profile' | 'history'>('history');
  const [selectedTicket, setSelectedTicket] = useState<Booking | null>(null);
  const [showCancelConfirm, setShowCancelConfirm] = useState<string | null>(null);

  if (!user) return null;

  const handleCancelTicket = (bookingId: string) => {
    updateBooking(bookingId, { status: 'cancelled' });
    setShowCancelConfirm(null);
    setSelectedTicket(null);
  };

  return (
    <div className="min-h-screen bg-background pb-20">
      <div className="bg-card border-b border-border">
        <div className="max-w-7xl mx-auto px-4 py-4 flex items-center gap-4">
          <button onClick={() => setCurrentView('home')} className="p-2 hover:bg-secondary rounded-lg">
            <ChevronLeft className="w-6 h-6" />
          </button>
          <h2>My Account</h2>
        </div>
      </div>

      <div className="max-w-4xl mx-auto px-4 py-8">
        <div className="flex gap-4 border-b border-border mb-8">
          <button
            onClick={() => setActiveTab('profile')}
            className={`pb-4 px-4 transition-colors ${
              activeTab === 'profile' ? 'border-b-2 border-primary text-primary' : 'text-muted-foreground'
            }`}
          >
            <User className="w-5 h-5 inline-block mr-2" />
            Profile
          </button>
          <button
            onClick={() => setActiveTab('history')}
            className={`pb-4 px-4 transition-colors ${
              activeTab === 'history' ? 'border-b-2 border-primary text-primary' : 'text-muted-foreground'
            }`}
          >
            <Ticket className="w-5 h-5 inline-block mr-2" />
            Ticket History
          </button>
        </div>

        {activeTab === 'profile' ? (
          <div className="bg-card rounded-lg p-6">
            <div className="flex items-center gap-4 mb-6">
              <div className="w-20 h-20 bg-primary/20 rounded-full flex items-center justify-center">
                <User className="w-10 h-10 text-primary" />
              </div>
              <div>
                <h3 className="text-xl">{user.name}</h3>
                <p className="text-muted-foreground">{user.email}</p>
              </div>
            </div>

            <div className="space-y-4">
              <div>
                <label className="block text-sm mb-2">Full Name</label>
                <input
                  type="text"
                  defaultValue={user.name}
                  className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-sm mb-2">Email</label>
                <input
                  type="email"
                  defaultValue={user.email}
                  className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none"
                />
              </div>
              <div>
                <label className="block text-sm mb-2">Phone Number</label>
                <input
                  type="tel"
                  defaultValue={user.phone}
                  className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none"
                />
              </div>
              <button className="w-full py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90">
                Save Changes
              </button>
            </div>
          </div>
        ) : (
          <div className="space-y-4">
            {bookings.length === 0 ? (
              <div className="text-center py-12 text-muted-foreground">
                <Ticket className="w-16 h-16 mx-auto mb-4 opacity-50" />
                <p>No bookings yet</p>
                <button
                  onClick={() => setCurrentView('home')}
                  className="mt-4 px-6 py-2 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90"
                >
                  Browse Movies
                </button>
              </div>
            ) : (
              bookings.map(booking => (
                <div
                  key={booking.id}
                  className="bg-card rounded-lg p-4 flex gap-4 cursor-pointer hover:bg-card/80 transition-colors"
                  onClick={() => setSelectedTicket(booking)}
                >
                  <img src={booking.moviePoster} alt={booking.movieTitle} className="w-24 h-36 object-cover rounded" />
                  <div className="flex-1">
                    <h3 className="mb-2">{booking.movieTitle}</h3>
                    <p className="text-sm text-muted-foreground mb-1">
                      {booking.date} • {booking.showtime} • {booking.room}
                    </p>
                    <p className="text-sm text-muted-foreground mb-2">
                      Seats: {booking.seats.join(', ')}
                    </p>
                    <span
                      className={`inline-block px-3 py-1 rounded text-sm ${
                        booking.status === 'valid'
                          ? 'bg-green-500/20 text-green-400'
                          : booking.status === 'used'
                          ? 'bg-blue-500/20 text-blue-400'
                          : 'bg-destructive/20 text-destructive'
                      }`}
                    >
                      {booking.status.toUpperCase()}
                    </span>
                  </div>
                  <div className="text-right">
                    <p className="text-xl text-primary">${booking.total.toFixed(2)}</p>
                  </div>
                </div>
              ))
            )}
          </div>
        )}
      </div>

      {selectedTicket && (
        <div
          className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4"
          onClick={() => setSelectedTicket(null)}
        >
          <div className="bg-card rounded-lg p-6 max-w-md w-full" onClick={e => e.stopPropagation()}>
            <h3 className="text-2xl mb-6 text-center">E-Ticket</h3>

            <div className="bg-background rounded-lg p-6 mb-6">
              <div className="flex items-center justify-center mb-6">
                <div className="w-48 h-48 bg-white p-4 rounded-lg flex items-center justify-center">
                  <QrCode className="w-full h-full" />
                </div>
              </div>

              <div className="space-y-3 text-center border-t border-border pt-4">
                <div>
                  <p className="text-sm text-muted-foreground">Booking ID</p>
                  <p className="text-lg">{selectedTicket.id}</p>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Movie</p>
                  <p>{selectedTicket.movieTitle}</p>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-muted-foreground">Date</p>
                    <p>{selectedTicket.date}</p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Time</p>
                    <p>{selectedTicket.showtime}</p>
                  </div>
                </div>
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-muted-foreground">Room</p>
                    <p>{selectedTicket.room}</p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Seats</p>
                    <p>{selectedTicket.seats.join(', ')}</p>
                  </div>
                </div>
              </div>
            </div>

            <div className="space-y-3">
              {selectedTicket.status === 'valid' && (
                <button
                  onClick={() => setShowCancelConfirm(selectedTicket.id)}
                  className="w-full py-3 bg-destructive text-destructive-foreground rounded-lg hover:bg-destructive/90"
                >
                  Cancel Ticket
                </button>
              )}
              <button
                onClick={() => setSelectedTicket(null)}
                className="w-full py-3 bg-secondary rounded-lg hover:bg-secondary/80"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}

      {showCancelConfirm && (
        <div
          className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4"
          onClick={() => setShowCancelConfirm(null)}
        >
          <div className="bg-card rounded-lg p-6 max-w-sm w-full" onClick={e => e.stopPropagation()}>
            <h3 className="text-xl mb-4">Cancel Ticket?</h3>
            <p className="text-muted-foreground mb-6">
              Are you sure? This will release your seats. Refund will be processed within 3-5 business days.
            </p>
            <div className="flex gap-3">
              <button
                onClick={() => setShowCancelConfirm(null)}
                className="flex-1 py-3 bg-secondary rounded-lg hover:bg-secondary/80"
              >
                Keep Ticket
              </button>
              <button
                onClick={() => handleCancelTicket(showCancelConfirm)}
                className="flex-1 py-3 bg-destructive text-destructive-foreground rounded-lg hover:bg-destructive/90"
              >
                Cancel Ticket
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
