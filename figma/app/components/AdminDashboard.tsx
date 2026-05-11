import { useState } from 'react';
import { useApp } from './AppContext';
import {
  LayoutDashboard,
  Film,
  Calendar,
  BarChart3,
  Settings,
  LogOut,
  Plus,
  Edit,
  Trash2,
  DollarSign,
  Ticket,
  TrendingUp,
  X,
  AlertCircle,
} from 'lucide-react';
import { LineChart, Line, BarChart, Bar, XAxis, YAxis, CartesianGrid, Tooltip, ResponsiveContainer } from 'recharts';

export function AdminDashboard() {
  const { setCurrentView, setUser, movies } = useApp();
  const [activeTab, setActiveTab] = useState('dashboard');
  const [showMovieModal, setShowMovieModal] = useState(false);
  const [showError, setShowError] = useState(false);
  const [errorMessage, setErrorMessage] = useState('');

  const revenueData = [
    { date: 'Apr 7', revenue: 12400 },
    { date: 'Apr 14', revenue: 15600 },
    { date: 'Apr 21', revenue: 18200 },
    { date: 'Apr 28', revenue: 14800 },
    { date: 'May 5', revenue: 21500 },
  ];

  const topMovies = [
    { title: 'Galactic Odyssey', revenue: 45600 },
    { title: 'Dragon Warriors', revenue: 38200 },
    { title: 'Shadow Detective', revenue: 32400 },
    { title: 'Quantum Realm', revenue: 28900 },
    { title: 'Love in Paris', revenue: 24100 },
  ];

  const handleLogout = () => {
    setUser(null);
    setCurrentView('auth');
  };

  const handleAddShowtime = (movieId: string, roomId: string, time: string) => {
    const testTime = '14:30';
    const existingTime = '13:00';

    if (time === testTime && roomId === 'r1') {
      setErrorMessage('Room Conflict: Time overlaps with another movie including cleaning time.');
      setShowError(true);
      return;
    }

    setShowError(false);
  };

  return (
    <div className="min-h-screen bg-background flex">
      <div className="w-64 bg-card border-r border-border flex flex-col">
        <div className="p-6 border-b border-border">
          <h2 className="text-xl">CinemaX Admin</h2>
        </div>

        <nav className="flex-1 p-4 space-y-2">
          {[
            { id: 'dashboard', icon: LayoutDashboard, label: 'Dashboard' },
            { id: 'movies', icon: Film, label: 'Movies' },
            { id: 'showtimes', icon: Calendar, label: 'Showtimes' },
            { id: 'reports', icon: BarChart3, label: 'Reports' },
            { id: 'settings', icon: Settings, label: 'Settings' },
          ].map(item => (
            <button
              key={item.id}
              onClick={() => setActiveTab(item.id)}
              className={`w-full flex items-center gap-3 px-4 py-3 rounded-lg transition-colors ${
                activeTab === item.id ? 'bg-primary text-primary-foreground' : 'hover:bg-secondary'
              }`}
            >
              <item.icon className="w-5 h-5" />
              {item.label}
            </button>
          ))}
        </nav>

        <div className="p-4 border-t border-border">
          <button
            onClick={handleLogout}
            className="w-full flex items-center gap-3 px-4 py-3 rounded-lg hover:bg-secondary"
          >
            <LogOut className="w-5 h-5" />
            Logout
          </button>
        </div>
      </div>

      <div className="flex-1 overflow-auto">
        <div className="p-8">
          {activeTab === 'dashboard' && (
            <>
              <h1 className="text-3xl mb-8">Dashboard Overview</h1>

              <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
                <div className="bg-card rounded-lg p-6 border border-border">
                  <div className="flex items-center justify-between mb-4">
                    <DollarSign className="w-8 h-8 text-primary" />
                    <TrendingUp className="w-5 h-5 text-green-500" />
                  </div>
                  <h3 className="text-2xl mb-1">$128,450</h3>
                  <p className="text-sm text-muted-foreground">Total Revenue (30d)</p>
                </div>

                <div className="bg-card rounded-lg p-6 border border-border">
                  <div className="flex items-center justify-between mb-4">
                    <Ticket className="w-8 h-8 text-primary" />
                    <TrendingUp className="w-5 h-5 text-green-500" />
                  </div>
                  <h3 className="text-2xl mb-1">8,542</h3>
                  <p className="text-sm text-muted-foreground">Tickets Sold (30d)</p>
                </div>

                <div className="bg-card rounded-lg p-6 border border-border">
                  <div className="flex items-center justify-between mb-4">
                    <Film className="w-8 h-8 text-primary" />
                  </div>
                  <h3 className="text-2xl mb-1">{movies.length}</h3>
                  <p className="text-sm text-muted-foreground">Active Movies</p>
                </div>
              </div>

              <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
                <div className="bg-card rounded-lg p-6 border border-border">
                  <h3 className="text-xl mb-4">Revenue Trend (Last 30 Days)</h3>
                  <ResponsiveContainer width="100%" height={300}>
                    <LineChart data={revenueData}>
                      <CartesianGrid strokeDasharray="3 3" stroke="#333" />
                      <XAxis dataKey="date" stroke="#888" />
                      <YAxis stroke="#888" />
                      <Tooltip
                        contentStyle={{ backgroundColor: '#16161f', border: '1px solid #333' }}
                        labelStyle={{ color: '#f5f5f7' }}
                      />
                      <Line type="monotone" dataKey="revenue" stroke="#d4af37" strokeWidth={2} />
                    </LineChart>
                  </ResponsiveContainer>
                </div>

                <div className="bg-card rounded-lg p-6 border border-border">
                  <h3 className="text-xl mb-4">Top 5 Highest Grossing Movies</h3>
                  <ResponsiveContainer width="100%" height={300}>
                    <BarChart data={topMovies} layout="vertical">
                      <CartesianGrid strokeDasharray="3 3" stroke="#333" />
                      <XAxis type="number" stroke="#888" />
                      <YAxis dataKey="title" type="category" width={120} stroke="#888" />
                      <Tooltip
                        contentStyle={{ backgroundColor: '#16161f', border: '1px solid #333' }}
                        labelStyle={{ color: '#f5f5f7' }}
                      />
                      <Bar dataKey="revenue" fill="#d4af37" />
                    </BarChart>
                  </ResponsiveContainer>
                </div>
              </div>
            </>
          )}

          {activeTab === 'movies' && (
            <>
              <div className="flex items-center justify-between mb-8">
                <h1 className="text-3xl">Movie Management</h1>
                <button
                  onClick={() => setShowMovieModal(true)}
                  className="flex items-center gap-2 px-6 py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90"
                >
                  <Plus className="w-5 h-5" />
                  Add Movie
                </button>
              </div>

              <div className="bg-card rounded-lg border border-border overflow-hidden">
                <table className="w-full">
                  <thead className="bg-secondary">
                    <tr>
                      <th className="text-left p-4">ID</th>
                      <th className="text-left p-4">Poster</th>
                      <th className="text-left p-4">Title</th>
                      <th className="text-left p-4">Genre</th>
                      <th className="text-left p-4">Duration</th>
                      <th className="text-left p-4">Actions</th>
                    </tr>
                  </thead>
                  <tbody>
                    {movies.map(movie => (
                      <tr key={movie.id} className="border-t border-border hover:bg-secondary/50">
                        <td className="p-4">{movie.id}</td>
                        <td className="p-4">
                          <img src={movie.poster} alt={movie.title} className="w-12 h-16 object-cover rounded" />
                        </td>
                        <td className="p-4">{movie.title}</td>
                        <td className="p-4">{movie.genre}</td>
                        <td className="p-4">{movie.duration} min</td>
                        <td className="p-4">
                          <div className="flex gap-2">
                            <button className="p-2 hover:bg-secondary rounded">
                              <Edit className="w-4 h-4" />
                            </button>
                            <button className="p-2 hover:bg-destructive/20 text-destructive rounded">
                              <Trash2 className="w-4 h-4" />
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
            </>
          )}

          {activeTab === 'showtimes' && (
            <>
              <h1 className="text-3xl mb-8">Showtime Scheduling</h1>

              {showError && (
                <div className="mb-6 p-4 bg-destructive/20 border border-destructive rounded-lg flex items-center gap-3">
                  <AlertCircle className="w-5 h-5 text-destructive flex-shrink-0" />
                  <p className="text-destructive flex-1">{errorMessage}</p>
                  <button onClick={() => setShowError(false)}>
                    <X className="w-5 h-5 text-destructive" />
                  </button>
                </div>
              )}

              <div className="bg-card rounded-lg p-6 border border-border">
                <h3 className="text-xl mb-4">Add New Showtime</h3>
                <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
                  <div>
                    <label className="block text-sm mb-2">Movie</label>
                    <select className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none">
                      {movies.map(m => (
                        <option key={m.id} value={m.id}>
                          {m.title}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm mb-2">Room</label>
                    <select className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none">
                      <option value="r1">Room 1</option>
                      <option value="r2">Room 2</option>
                      <option value="r3">Room 3</option>
                    </select>
                  </div>
                  <div>
                    <label className="block text-sm mb-2">Start Time</label>
                    <input
                      type="time"
                      className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none"
                    />
                  </div>
                  <div className="flex items-end">
                    <button
                      onClick={() => handleAddShowtime('1', 'r1', '14:30')}
                      className="w-full px-6 py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90"
                    >
                      Add Showtime
                    </button>
                  </div>
                </div>
              </div>
            </>
          )}
        </div>
      </div>

      {showMovieModal && (
        <div
          className="fixed inset-0 bg-black/80 flex items-center justify-center z-50 p-4"
          onClick={() => setShowMovieModal(false)}
        >
          <div className="bg-card rounded-lg p-6 max-w-2xl w-full max-h-[90vh] overflow-auto" onClick={e => e.stopPropagation()}>
            <h3 className="text-2xl mb-6">Add New Movie</h3>
            <div className="space-y-4">
              <div>
                <label className="block text-sm mb-2">Movie Title</label>
                <input
                  type="text"
                  className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none"
                  placeholder="Enter movie title"
                />
              </div>
              <div>
                <label className="block text-sm mb-2">Description</label>
                <textarea
                  rows={3}
                  className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none"
                  placeholder="Enter movie description"
                />
              </div>
              <div className="grid grid-cols-2 gap-4">
                <div>
                  <label className="block text-sm mb-2">Genre</label>
                  <input
                    type="text"
                    className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none"
                    placeholder="e.g., Action, Drama"
                  />
                </div>
                <div>
                  <label className="block text-sm mb-2">Duration (minutes)</label>
                  <input
                    type="number"
                    className="w-full px-4 py-3 rounded-lg bg-input-background border border-border focus:border-primary focus:outline-none"
                    placeholder="120"
                  />
                </div>
              </div>
              <div>
                <label className="block text-sm mb-2">Upload Poster</label>
                <div className="border-2 border-dashed border-border rounded-lg p-8 text-center hover:border-primary transition-colors cursor-pointer">
                  <p className="text-muted-foreground">Drag & drop poster image here or click to browse</p>
                </div>
              </div>
              <div className="flex gap-3">
                <button
                  onClick={() => setShowMovieModal(false)}
                  className="flex-1 py-3 bg-secondary rounded-lg hover:bg-secondary/80"
                >
                  Cancel
                </button>
                <button className="flex-1 py-3 bg-primary text-primary-foreground rounded-lg hover:bg-primary/90">
                  Add Movie
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
