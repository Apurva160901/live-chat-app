import { useEffect, useState } from 'react';
import Login from './Login';
import Dashboard from './Dashboard';
import { applyTheme } from './ThemePicker';
import './App.css';

// Read any saved login from the browser so a refresh keeps you logged in.
function loadAuth() {
  try {
    return JSON.parse(localStorage.getItem('chat-auth'));
  } catch {
    return null;
  }
}

export default function App() {
  const [auth, setAuth] = useState(loadAuth);

  // Apply the saved theme on first load.
  useEffect(() => {
    applyTheme(localStorage.getItem('chat-theme') || 'Dark');
  }, []);

  function handleAuth(a) {
    localStorage.setItem('chat-auth', JSON.stringify(a));
    setAuth(a);
  }

  function handleLogout() {
    localStorage.removeItem('chat-auth');
    setAuth(null);
  }

  // Merge updates into auth (e.g. after uploading an avatar) and persist them.
  function handleAuthUpdate(patch) {
    setAuth((prev) => {
      const next = { ...prev, ...patch };
      localStorage.setItem('chat-auth', JSON.stringify(next));
      return next;
    });
  }

  if (!auth?.token) return <Login onAuth={handleAuth} />;
  return <Dashboard auth={auth} onLogout={handleLogout} onAuthUpdate={handleAuthUpdate} />;
}
