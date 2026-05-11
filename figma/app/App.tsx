import { AppProvider, useApp } from './components/AppContext';
import { AuthScreen } from './components/AuthScreen';
import { HomePage } from './components/HomePage';
import { ShowtimeScreen } from './components/ShowtimeScreen';
import { SeatsScreen } from './components/SeatsScreen';
import { CheckoutScreen } from './components/CheckoutScreen';
import { ProfileScreen } from './components/ProfileScreen';
import { AdminDashboard } from './components/AdminDashboard';
import { StaffPOS } from './components/StaffPOS';
import { BottomNav } from './components/BottomNav';

function AppContent() {
  const { currentView, user } = useApp();

  return (
    <div className="size-full bg-background text-foreground">
      {currentView === 'auth' && <AuthScreen />}
      {currentView === 'home' && user?.role === 'customer' && <HomePage />}
      {currentView === 'showtime' && user?.role === 'customer' && <ShowtimeScreen />}
      {currentView === 'seats' && user?.role === 'customer' && <SeatsScreen />}
      {currentView === 'checkout' && user?.role === 'customer' && <CheckoutScreen />}
      {currentView === 'profile' && user?.role === 'customer' && <ProfileScreen />}
      {currentView === 'admin' && user?.role === 'admin' && <AdminDashboard />}
      {currentView === 'staff' && user?.role === 'staff' && <StaffPOS />}
      <BottomNav />
    </div>
  );
}

export default function App() {
  return (
    <AppProvider>
      <AppContent />
    </AppProvider>
  );
}