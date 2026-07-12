import { useEffect, useState } from 'react';

// Selectable chat themes. Each just overrides a few CSS variables.
export const THEMES = {
  Dark:     { '--bg': '#0f172a', '--panel': '#1e293b', '--panel-2': '#152238', '--accent': '#6366f1', '--accent-2': '#4f46e5', '--text': '#e2e8f0', '--muted': '#94a3b8', '--input-bg': '#0b1220', '--border': '#334155' },
  Light:    { '--bg': '#f1f5f9', '--panel': '#ffffff', '--panel-2': '#f8fafc', '--accent': '#6366f1', '--accent-2': '#4f46e5', '--text': '#0f172a', '--muted': '#64748b', '--input-bg': '#f8fafc', '--border': '#cbd5e1' },
  WhatsApp: { '--bg': '#0b141a', '--panel': '#202c33', '--panel-2': '#111b21', '--accent': '#25d366', '--accent-2': '#1da851', '--text': '#e9edef', '--muted': '#8696a0', '--input-bg': '#2a3942', '--border': '#2a3942' },
  Ocean:    { '--bg': '#0c2231', '--panel': '#12384d', '--panel-2': '#0a2836', '--accent': '#06b6d4', '--accent-2': '#0891b2', '--text': '#e0f2fe', '--muted': '#7dd3fc', '--input-bg': '#0a2836', '--border': '#1e5570' },
};

export function applyTheme(name) {
  const vars = THEMES[name] || THEMES.Dark;
  for (const [key, value] of Object.entries(vars)) {
    document.documentElement.style.setProperty(key, value);
  }
}

export default function ThemePicker() {
  const [theme, setTheme] = useState(() => localStorage.getItem('chat-theme') || 'Dark');

  useEffect(() => {
    applyTheme(theme);
    localStorage.setItem('chat-theme', theme);
  }, [theme]);

  return (
    <select
      className="theme-select"
      value={theme}
      onChange={(e) => setTheme(e.target.value)}
      title="Change theme"
    >
      {Object.keys(THEMES).map((t) => (
        <option key={t} value={t}>{t}</option>
      ))}
    </select>
  );
}
