import { useState } from 'react';
import { apiPost } from './api';
import ThemePicker from './ThemePicker';

/**
 * Login / Register screen. On success it calls onAuth(...) with the token + profile,
 * which the parent stores (so you stay logged in across refreshes).
 */
export default function Login({ onAuth }) {
  const [mode, setMode] = useState('login'); // 'login' | 'register'
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [error, setError] = useState('');
  const [busy, setBusy] = useState(false);

  async function submit(e) {
    e.preventDefault();
    setError('');
    setBusy(true);
    try {
      const path = mode === 'login' ? '/api/auth/login' : '/api/auth/register';
      const body = mode === 'login'
        ? { username, password }
        : { username, password, displayName };
      const data = await apiPost(path, body);
      onAuth({
        token: data.token,
        username: data.username,
        displayName: data.displayName,
        avatarUrl: data.avatarUrl,
      });
    } catch (err) {
      setError(mode === 'login'
        ? 'Invalid username or password'
        : (err.message || 'Registration failed'));
    } finally {
      setBusy(false);
    }
  }

  return (
    <div className="center-screen">
      <form className="join-card" onSubmit={submit}>
        <h1>💬 Live Chat</h1>
        <p>{mode === 'login' ? 'Log in to continue' : 'Create your account'}</p>

        {mode === 'register' && (
          <input
            placeholder="Display name (optional)"
            value={displayName}
            onChange={(e) => setDisplayName(e.target.value)}
            maxLength={30}
          />
        )}
        <input
          placeholder="Username"
          value={username}
          onChange={(e) => setUsername(e.target.value)}
          maxLength={20}
          autoFocus
        />
        <input
          type="password"
          placeholder="Password"
          value={password}
          onChange={(e) => setPassword(e.target.value)}
        />

        {error && <div className="error">{error}</div>}

        <button type="submit" disabled={busy || !username || !password}>
          {busy ? 'Please wait…' : (mode === 'login' ? 'Log in' : 'Register')}
        </button>

        <div className="switch">
          {mode === 'login' ? "New here? " : 'Have an account? '}
          <a onClick={() => { setMode(mode === 'login' ? 'register' : 'login'); setError(''); }}>
            {mode === 'login' ? 'Create an account' : 'Log in'}
          </a>
        </div>

        <div className="theme-row">Theme: <ThemePicker /></div>
      </form>
    </div>
  );
}
