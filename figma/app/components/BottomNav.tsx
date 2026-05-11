import { useApp } from './AppContext';
import { Home, Film, User, Ticket } from 'lucide-react';

export function BottomNav() {
  const { currentView, setCurrentView, user } = useApp();

  if (!user || user.role !== 'customer') return null;
  if (['showtime', 'seats', 'checkout'].includes(currentView)) return null;

  const navItems = [
    { id: 'home', icon: Home, label: 'Home' },
    { id: 'profile', icon: User, label: 'Profile' },
  ];

  return (
    <div className="fixed bottom-0 left-0 right-0 bg-card border-t border-border md:hidden z-20">
      <div className="flex items-center justify-around py-3">
        {navItems.map(item => (
          <button
            key={item.id}
            onClick={() => setCurrentView(item.id)}
            className={`flex flex-col items-center gap-1 px-6 py-2 transition-colors ${
              currentView === item.id ? 'text-primary' : 'text-muted-foreground'
            }`}
          >
            <item.icon className="w-6 h-6" />
            <span className="text-xs">{item.label}</span>
          </button>
        ))}
      </div>
    </div>
  );
}
