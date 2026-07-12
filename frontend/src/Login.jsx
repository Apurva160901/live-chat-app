import { useState } from 'react';
import { apiPost } from './api';
import ThemePicker from './ThemePicker';

/**
 * Login / Register / Forgot-password / Reset-password screen.
 * On successful login or register it calls onAuth(...) with the token + profile.
 */
export default function Login({ onAuth }) {
  const [mode, setMode] = useState('login'); // 'login' | 'register' | 'forgot' | 'reset'
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [displayName, setDisplayName] = useState('');
  const [email, setEmail] = useState('');
  const [token, setToken] = useState('');
  const [newPassword, setNewPassword] = useState('');
  const [error, setError] = useState('');
  const [info, setInfo] = useState('');
  const [busy, setBusy] = useState(false);

  function go(newMode) {
    setMode(newMode);
    setError('');
  }

  async function submit(e) {
    e.preventDefault();
    setError('');
    setBusy(true);
    try {
      if (mode === 'login' || mode === 'register') {
        const path = mode === 'login' ? '/api/auth/login' : '/api/auth/register';
        const body = mode === 'login'
          ? { username, password }
          : { username, password, displayName, email };
        const data = await apiPost(path, body);
        onAuth({ token: data.token, username: data.username, displayName: data.displayName, avatarUrl: data.avatarUrl });
      } else if (mode === 'forgot') {
        const data = await apiPost('/api/auth/forgot-password', { username });
        setToken(data.token || '');            // demo: token returned (prod: emailed)
        setInfo('Reset token generated. In a real app this is emailed — for the demo it is filled in below.');
        setMode('reset');
      } else if (mode === 'reset') {
        const data = await apiPost('/api/auth/reset-password', { token, newPassword });
        setInfo(data.message || 'Password reset. Please log in.');
        setPassword('');
        setNewPassword('');
        setMode('login');
      }
    } catch (err) {
      setError(err.message || 'Something went wrong');
    } finally {
      setBusy(false);
    }
  }

  const title = {
    login: 'Log in to continue',
    register: 'Create your account',
    forgot: 'Forgot password',
    reset: 'Set a new password',
  }[mode];

  return (
    <div className="center-screen">
      <form className="join-card" onSubmit={submit}>
        <h1>💬 Live Chat</h1>
        <p>{title}</p>

        {info && <div className="info">{info}</div>}

        {mode === 'register' && (
          <input placeholder="Display name (optional)" value={displayName}
                 onChange={(e) => setDisplayName(e.target.value)} maxLength={30} />
        )}
        {mode === 'register' && (
          <input type="email" placeholder="Email (for password reset)" value={email}
                 onChange={(e) => setEmail(e.target.value)} />
        )}

        {(mode === 'login' || mode === 'register' || mode === 'forgot') && (
          <input placeholder="Username" value={username} autoFocus
                 onChange={(e) => setUsername(e.target.value)} maxLength={20} />
        )}

        {(mode === 'login' || mode === 'register') && (
          <input type="password" placeholder="Password" value={password}
                 onChange={(e) => setPassword(e.target.value)} />
        )}

        {mode === 'reset' && (
          <>
            <input placeholder="Reset token" value={token}
                   onChange={(e) => setToken(e.target.value)} />
            <input type="password" placeholder="New password" value={newPassword} autoFocus
                   onChange={(e) => setNewPassword(e.target.value)} />
          </>
        )}

        {error && <div className="error">{error}</div>}

        <button type="submit" disabled={busy}>
          {busy ? 'Please wait…'
            : mode === 'login' ? 'Log in'
            : mode === 'register' ? 'Register'
            : mode === 'forgot' ? 'Get reset token'
            : 'Reset password'}
        </button>

        <div className="switch">
          {mode === 'login' && (
            <>
              <a onClick={() => go('register')}>Create an account</a>
              {' · '}
              <a onClick={() => go('forgot')}>Forgot password?</a>
            </>
          )}
          {mode === 'register' && (<>Have an account? <a onClick={() => go('login')}>Log in</a></>)}
          {(mode === 'forgot' || mode === 'reset') && (<a onClick={() => go('login')}>← Back to log in</a>)}
        </div>

        <div className="theme-row">Theme: <ThemePicker /></div>
      </form>
    </div>
  );
}
