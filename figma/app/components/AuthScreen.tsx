import { useState } from 'react';
import { useApp } from './AppContext';
import { Eye, EyeOff, Film } from 'lucide-react';

export function AuthScreen() {
  const { setUser, setCurrentView } = useApp();
  const [isSignUp, setIsSignUp] = useState(false);
  const [showPassword, setShowPassword] = useState(false);
  const [formData, setFormData] = useState({
    name: '',
    email: '',
    phone: '',
    password: '',
  });

  const handleSubmit = (e: React.FormEvent) => {
    e.preventDefault();
    setUser({
      id: '1',
      name: formData.name || 'John Doe',
      email: formData.email,
      phone: formData.phone,
      role: 'customer',
    });
    setCurrentView('home');
  };

  return (
    <div className="min-h-screen flex">
      <div className="hidden lg:flex lg:w-1/2 bg-cover bg-center relative"
           style={{ backgroundImage: 'url(https://images.unsplash.com/photo-1489599849927-2ee91cede3ba?w=1200&h=1600&fit=crop)' }}>
        <div className="absolute inset-0 bg-gradient-to-r from-background/90 to-background/40" />
        <div className="relative z-10 p-12 flex flex-col justify-center">
          <Film className="w-16 h-16 text-primary mb-6" />
          <h1 className="text-4xl mb-4">Welcome to CinemaX</h1>
          <p className="text-xl text-muted-foreground">Your premium cinema experience awaits</p>
        </div>
      </div>

      <div className="w-full lg:w-1/2 flex items-center justify-center p-8 bg-card">
        <div className="w-full max-w-md space-y-8">
          <div className="text-center lg:hidden">
            <Film className="w-12 h-12 text-primary mx-auto mb-4" />
            <h2 className="text-3xl">CinemaX</h2>
          </div>

          <div className="flex gap-4 border-b border-border">
            <button
              onClick={() => setIsSignUp(false)}
              className={`pb-4 px-2 transition-colors ${!isSignUp ? 'border-b-2 border-primary text-primary' : 'text-muted-foreground'}`}
            >
              Sign In
            </button>
            <button
              onClick={() => setIsSignUp(true)}
              className={`pb-4 px-2 transition-colors ${isSignUp ? 'border-b-2 border-primary text-primary' : 'text-muted-foreground'}`}
            >
              Sign Up
            </button>
          </div>

          <form onSubmit={handleSubmit} className="space-y-6">
            {isSignUp && (
              <div>
                <label className="block mb-2 text-sm">Full Name</label>
                <input
                  type="text"
                  value={formData.name}
                  onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                  className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none transition-colors"
                  placeholder="Enter your full name"
                />
              </div>
            )}

            <div>
              <label className="block mb-2 text-sm">Email</label>
              <input
                type="email"
                value={formData.email}
                onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none transition-colors"
                placeholder="Enter your email"
              />
            </div>

            {isSignUp && (
              <div>
                <label className="block mb-2 text-sm">Phone Number</label>
                <input
                  type="tel"
                  value={formData.phone}
                  onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                  className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none transition-colors"
                  placeholder="Enter your phone number"
                />
              </div>
            )}

            <div>
              <label className="block mb-2 text-sm">Password</label>
              <div className="relative">
                <input
                  type={showPassword ? 'text' : 'password'}
                  value={formData.password}
                  onChange={(e) => setFormData({ ...formData, password: e.target.value })}
                  className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none transition-colors"
                  placeholder="Enter your password"
                />
                <button
                  type="button"
                  onClick={() => setShowPassword(!showPassword)}
                  className="absolute right-3 top-1/2 -translate-y-1/2 text-muted-foreground hover:text-foreground"
                >
                  {showPassword ? <EyeOff className="w-5 h-5" /> : <Eye className="w-5 h-5" />}
                </button>
              </div>
            </div>

            <button
              type="submit"
              className="w-full py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90 transition-colors"
            >
              {isSignUp ? 'Create Account' : 'Sign In'}
            </button>

            <div className="relative">
              <div className="absolute inset-0 flex items-center">
                <div className="w-full border-t border-border" />
              </div>
              <div className="relative flex justify-center text-sm">
                <span className="px-2 bg-card text-muted-foreground">Or continue with</span>
              </div>
            </div>

            <div className="grid grid-cols-2 gap-4">
              <button
                type="button"
                className="py-3 border border-border rounded-lg hover:bg-secondary transition-colors"
              >
                Google
              </button>
              <button
                type="button"
                className="py-3 border border-border rounded-lg hover:bg-secondary transition-colors"
              >
                Facebook
              </button>
            </div>
          </form>

          <div className="text-center space-y-2">
            <button
              onClick={() => {
                setUser({ id: 'admin', name: 'Admin User', email: 'admin@cinema.com', phone: '', role: 'admin' });
                setCurrentView('admin');
              }}
              className="text-sm text-muted-foreground hover:text-primary"
            >
              Admin Login
            </button>
            <span className="mx-2 text-muted-foreground">|</span>
            <button
              onClick={() => {
                setUser({ id: 'staff', name: 'Staff User', email: 'staff@cinema.com', phone: '', role: 'staff' });
                setCurrentView('staff');
              }}
              className="text-sm text-muted-foreground hover:text-primary"
            >
              Staff Login
            </button>
          </div>
        </div>
      </div>
    </div>
  );
}
