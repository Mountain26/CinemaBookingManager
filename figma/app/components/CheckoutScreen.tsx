import { useState } from 'react';
import { useApp } from './AppContext';
import { ChevronLeft, Plus, Minus, CreditCard, Smartphone } from 'lucide-react';
import { Booking } from './types';

export function CheckoutScreen() {
  const {
    selectedMovie,
    selectedShowtime,
    selectedSeats,
    combos,
    selectedCombos,
    setSelectedCombos,
    addBooking,
    setCurrentView,
    rooms,
  } = useApp();

  const [paymentMethod, setPaymentMethod] = useState('vnpay');
  const [showConfirmation, setShowConfirmation] = useState(false);

  if (!selectedMovie || !selectedShowtime || selectedSeats.length === 0) {
    setCurrentView('home');
    return null;
  }

  const room = rooms.find(r => r.id === selectedShowtime.roomId);
  const ticketPrice = selectedSeats.reduce((sum, seat) => sum + (seat.isVip ? 15 : 10), 0);
  const comboPrice = selectedCombos.reduce((sum, sc) => {
    const combo = combos.find(c => c.id === sc.id);
    return sum + (combo?.price || 0) * sc.quantity;
  }, 0);
  const subtotal = ticketPrice + comboPrice;
  const tax = subtotal * 0.1;
  const total = subtotal + tax;

  const updateComboQuantity = (id: string, delta: number) => {
    const existing = selectedCombos.find(c => c.id === id);
    if (existing) {
      const newQuantity = existing.quantity + delta;
      if (newQuantity <= 0) {
        setSelectedCombos(selectedCombos.filter(c => c.id !== id));
      } else {
        setSelectedCombos(selectedCombos.map(c => c.id === id ? { ...c, quantity: newQuantity } : c));
      }
    } else if (delta > 0) {
      setSelectedCombos([...selectedCombos, { id, quantity: 1 }]);
    }
  };

  const handleConfirmPayment = () => {
    const booking: Booking = {
      id: `BK${Date.now()}`,
      movieId: selectedMovie.id,
      movieTitle: selectedMovie.title,
      moviePoster: selectedMovie.poster,
      showtime: selectedShowtime.time,
      date: selectedShowtime.date,
      room: room?.name || '',
      seats: selectedSeats.map(s => `${s.row}${s.number}`),
      combos: selectedCombos.map(sc => {
        const combo = combos.find(c => c.id === sc.id)!;
        return {
          id: sc.id,
          name: combo.name,
          quantity: sc.quantity,
          price: combo.price,
        };
      }),
      total,
      status: 'valid',
      qrCode: `QR${Date.now()}`,
    };
    addBooking(booking);
    setShowConfirmation(true);
  };

  if (showConfirmation) {
    return (
      <div className="min-h-screen bg-background flex items-center justify-center p-4">
        <div className="bg-card rounded-lg p-8 max-w-md w-full text-center">
          <div className="w-20 h-20 bg-primary/20 rounded-full flex items-center justify-center mx-auto mb-4">
            <svg className="w-10 h-10 text-primary" fill="none" viewBox="0 0 24 24" stroke="currentColor">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
            </svg>
          </div>
          <h2 className="text-2xl mb-2">Payment Successful!</h2>
          <p className="text-muted-foreground mb-6">Your booking has been confirmed</p>
          <div className="space-y-3">
            <button
              onClick={() => setCurrentView('profile')}
              className="w-full py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90"
            >
              View My Tickets
            </button>
            <button
              onClick={() => setCurrentView('home')}
              className="w-full py-3 bg-secondary text-foreground rounded-lg hover:bg-secondary/80"
            >
              Back to Home
            </button>
          </div>
        </div>
      </div>
    );
  }

  return (
    <div className="min-h-screen bg-background pb-20">
      <div className="bg-card border-b border-border sticky top-0 z-10">
        <div className="max-w-7xl mx-auto px-4 py-4 flex items-center gap-4">
          <button onClick={() => setCurrentView('seats')} className="p-2 hover:bg-secondary rounded-lg">
            <ChevronLeft className="w-6 h-6" />
          </button>
          <h2>Checkout</h2>
        </div>
      </div>

      <div className="max-w-7xl mx-auto px-4 py-8 grid lg:grid-cols-3 gap-8">
        <div className="lg:col-span-2 space-y-6">
          <div className="bg-card rounded-lg p-6">
            <h3 className="text-xl mb-4">Food & Beverage Combos</h3>
            <div className="space-y-4">
              {combos.map(combo => {
                const selectedCombo = selectedCombos.find(c => c.id === combo.id);
                const quantity = selectedCombo?.quantity || 0;

                return (
                  <div key={combo.id} className="flex gap-4 pb-4 border-b border-border last:border-0">
                    <img src={combo.image} alt={combo.name} className="w-24 h-24 object-cover rounded-lg" />
                    <div className="flex-1">
                      <h4>{combo.name}</h4>
                      <p className="text-sm text-muted-foreground mb-2">{combo.description}</p>
                      <p className="text-primary">${combo.price.toFixed(2)}</p>
                    </div>
                    <div className="flex items-center gap-3">
                      <button
                        onClick={() => updateComboQuantity(combo.id, -1)}
                        className="w-8 h-8 rounded-full bg-secondary hover:bg-secondary/80 flex items-center justify-center"
                      >
                        <Minus className="w-4 h-4" />
                      </button>
                      <span className="w-8 text-center">{quantity}</span>
                      <button
                        onClick={() => updateComboQuantity(combo.id, 1)}
                        className="w-8 h-8 rounded-full bg-primary text-primary-foreground hover:bg-primary/90 flex items-center justify-center"
                      >
                        <Plus className="w-4 h-4" />
                      </button>
                    </div>
                  </div>
                );
              })}
            </div>
          </div>

          <div className="bg-card rounded-lg p-6">
            <h3 className="text-xl mb-4">Payment Method</h3>
            <div className="space-y-3">
              {[
                { id: 'vnpay', name: 'VNPay', icon: Smartphone },
                { id: 'momo', name: 'Momo', icon: Smartphone },
                { id: 'paypal', name: 'PayPal', icon: CreditCard },
                { id: 'card', name: 'Credit Card', icon: CreditCard },
              ].map(method => (
                <label
                  key={method.id}
                  className={`flex items-center gap-3 p-4 rounded-lg border cursor-pointer transition-colors ${
                    paymentMethod === method.id ? 'border-primary bg-primary/5' : 'border-border hover:border-primary/50'
                  }`}
                >
                  <input
                    type="radio"
                    name="payment"
                    value={method.id}
                    checked={paymentMethod === method.id}
                    onChange={(e) => setPaymentMethod(e.target.value)}
                    className="accent-primary"
                  />
                  <method.icon className="w-5 h-5" />
                  <span>{method.name}</span>
                </label>
              ))}
            </div>
          </div>
        </div>

        <div className="lg:col-span-1">
          <div className="bg-card rounded-lg p-6 sticky top-20">
            <h3 className="text-xl mb-4">Order Summary</h3>
            <div className="space-y-3 mb-4 pb-4 border-b border-border">
              <div className="flex items-start gap-3">
                <img src={selectedMovie.poster} alt={selectedMovie.title} className="w-16 h-24 object-cover rounded" />
                <div className="flex-1">
                  <h4 className="text-sm mb-1">{selectedMovie.title}</h4>
                  <p className="text-xs text-muted-foreground">
                    {selectedShowtime.date} • {selectedShowtime.time}
                  </p>
                  <p className="text-xs text-muted-foreground">{room?.name}</p>
                </div>
              </div>
              <div>
                <p className="text-sm text-muted-foreground">Seats</p>
                <p className="text-sm">{selectedSeats.map(s => `${s.row}${s.number}`).join(', ')}</p>
              </div>
            </div>

            {selectedCombos.length > 0 && (
              <div className="space-y-2 mb-4 pb-4 border-b border-border">
                <p className="text-sm">Food & Beverage</p>
                {selectedCombos.map(sc => {
                  const combo = combos.find(c => c.id === sc.id)!;
                  return (
                    <div key={sc.id} className="flex justify-between text-sm text-muted-foreground">
                      <span>{combo.name} x{sc.quantity}</span>
                      <span>${(combo.price * sc.quantity).toFixed(2)}</span>
                    </div>
                  );
                })}
              </div>
            )}

            <div className="space-y-2 mb-4 pb-4 border-b border-border">
              <div className="flex justify-between text-sm">
                <span>Subtotal</span>
                <span>${subtotal.toFixed(2)}</span>
              </div>
              <div className="flex justify-between text-sm text-muted-foreground">
                <span>Tax (10%)</span>
                <span>${tax.toFixed(2)}</span>
              </div>
            </div>

            <div className="flex justify-between text-xl mb-6">
              <span>Total</span>
              <span className="text-primary">${total.toFixed(2)}</span>
            </div>

            <button
              onClick={handleConfirmPayment}
              className="w-full py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90"
            >
              Confirm & Pay
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
