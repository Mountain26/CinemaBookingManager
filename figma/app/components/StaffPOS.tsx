import { useState } from 'react';
import { useApp } from './AppContext';
import { QrCode, Search, CheckCircle, XCircle, Printer, LogOut } from 'lucide-react';

export function StaffPOS() {
  const { bookings, updateBooking, setUser, setCurrentView } = useApp();
  const [searchQuery, setSearchQuery] = useState('');
  const [scannedTicket, setScannedTicket] = useState<string | null>(null);

  const handleSearch = () => {
    const booking = bookings.find(b => b.id === searchQuery);
    if (booking) {
      setScannedTicket(booking.id);
    }
  };

  const handleScan = () => {
    const validBooking = bookings.find(b => b.status === 'valid');
    if (validBooking) {
      setScannedTicket(validBooking.id);
    }
  };

  const handleMarkAsUsed = () => {
    if (scannedTicket) {
      updateBooking(scannedTicket, { status: 'used' });
    }
  };

  const handleLogout = () => {
    setUser(null);
    setCurrentView('auth');
  };

  const ticket = scannedTicket ? bookings.find(b => b.id === scannedTicket) : null;

  return (
    <div className="min-h-screen bg-background flex flex-col">
      <div className="bg-card border-b border-border p-4 flex items-center justify-between">
        <h1 className="text-2xl">CinemaX Staff Portal</h1>
        <button
          onClick={handleLogout}
          className="flex items-center gap-2 px-4 py-2 bg-secondary rounded-lg hover:bg-secondary/80"
        >
          <LogOut className="w-5 h-5" />
          Logout
        </button>
      </div>

      <div className="flex-1 flex items-center justify-center p-8">
        <div className="max-w-2xl w-full space-y-8">
          <div className="text-center">
            <h2 className="text-3xl mb-2">Ticket Verification</h2>
            <p className="text-muted-foreground">Scan or enter booking ID to verify tickets</p>
          </div>

          <div className="bg-card rounded-lg p-8 border border-border">
            <div className="space-y-6">
              <div>
                <label className="block text-sm mb-2">Enter Booking ID</label>
                <div className="flex gap-3">
                  <input
                    type="text"
                    value={searchQuery}
                    onChange={(e) => setSearchQuery(e.target.value)}
                    onKeyDown={(e) => e.key === 'Enter' && handleSearch()}
                    className="flex-1 px-4 py-4 rounded-lg bg-input-background border-2 border-border focus:border-primary focus:outline-none text-lg"
                    placeholder="Enter booking ID..."
                  />
                  <button
                    onClick={handleSearch}
                    className="px-8 py-4 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 flex items-center gap-2"
                  >
                    <Search className="w-5 h-5" />
                    Search
                  </button>
                </div>
              </div>

              <div className="relative">
                <div className="absolute inset-0 flex items-center">
                  <div className="w-full border-t border-border" />
                </div>
                <div className="relative flex justify-center text-sm">
                  <span className="px-4 bg-card text-muted-foreground">OR</span>
                </div>
              </div>

              <button
                onClick={handleScan}
                className="w-full py-8 border-2 border-dashed border-border rounded-lg hover:border-primary hover:bg-primary/5 transition-colors flex flex-col items-center gap-3"
              >
                <QrCode className="w-16 h-16 text-muted-foreground" />
                <span className="text-lg">Scan QR Code</span>
                <span className="text-sm text-muted-foreground">Click to simulate QR scan</span>
              </button>
            </div>
          </div>

          {ticket && (
            <div className="bg-card rounded-lg p-8 border-2 border-primary animate-in fade-in slide-in-from-bottom-4">
              {ticket.status === 'valid' ? (
                <>
                  <div className="flex items-center justify-center mb-6">
                    <div className="w-20 h-20 bg-green-500/20 rounded-full flex items-center justify-center">
                      <CheckCircle className="w-12 h-12 text-green-500" />
                    </div>
                  </div>
                  <h3 className="text-3xl text-green-500 text-center mb-6">Valid Ticket</h3>
                </>
              ) : ticket.status === 'used' ? (
                <>
                  <div className="flex items-center justify-center mb-6">
                    <div className="w-20 h-20 bg-blue-500/20 rounded-full flex items-center justify-center">
                      <CheckCircle className="w-12 h-12 text-blue-500" />
                    </div>
                  </div>
                  <h3 className="text-3xl text-blue-500 text-center mb-6">Already Used</h3>
                </>
              ) : (
                <>
                  <div className="flex items-center justify-center mb-6">
                    <div className="w-20 h-20 bg-destructive/20 rounded-full flex items-center justify-center">
                      <XCircle className="w-12 h-12 text-destructive" />
                    </div>
                  </div>
                  <h3 className="text-3xl text-destructive text-center mb-6">Invalid Ticket</h3>
                </>
              )}

              <div className="bg-background rounded-lg p-6 mb-6 space-y-3">
                <div className="grid grid-cols-2 gap-4">
                  <div>
                    <p className="text-sm text-muted-foreground">Booking ID</p>
                    <p className="text-lg">{ticket.id}</p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Status</p>
                    <p className={`text-lg ${
                      ticket.status === 'valid' ? 'text-green-500' :
                      ticket.status === 'used' ? 'text-blue-500' : 'text-destructive'
                    }`}>
                      {ticket.status.toUpperCase()}
                    </p>
                  </div>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Movie</p>
                  <p className="text-lg">{ticket.movieTitle}</p>
                </div>
                <div className="grid grid-cols-3 gap-4">
                  <div>
                    <p className="text-sm text-muted-foreground">Date</p>
                    <p>{ticket.date}</p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Time</p>
                    <p>{ticket.showtime}</p>
                  </div>
                  <div>
                    <p className="text-sm text-muted-foreground">Room</p>
                    <p>{ticket.room}</p>
                  </div>
                </div>
                <div>
                  <p className="text-sm text-muted-foreground">Seats</p>
                  <p>{ticket.seats.join(', ')}</p>
                </div>
              </div>

              <div className="grid grid-cols-2 gap-4">
                {ticket.status === 'valid' && (
                  <>
                    <button
                      onClick={handleMarkAsUsed}
                      className="py-4 bg-green-500 text-white rounded-lg hover:bg-green-600 flex items-center justify-center gap-2"
                    >
                      <CheckCircle className="w-5 h-5" />
                      Mark as Used
                    </button>
                    <button className="py-4 bg-secondary rounded-lg hover:bg-secondary/80 flex items-center justify-center gap-2">
                      <Printer className="w-5 h-5" />
                      Print Ticket
                    </button>
                  </>
                )}
                {ticket.status !== 'valid' && (
                  <button
                    onClick={() => setScannedTicket(null)}
                    className="col-span-2 py-4 bg-secondary rounded-lg hover:bg-secondary/80"
                  >
                    Scan Another Ticket
                  </button>
                )}
              </div>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}
