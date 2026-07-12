// Small helper for talking to the backend REST API with the JWT token.

// Empty = same origin. The Vite dev server proxies /api and /ws to the backend,
// so the browser makes same-origin requests (no CORS/WebSocket cross-origin issues).
export const API_BASE = '';

function authHeaders(token) {
  return token ? { Authorization: `Bearer ${token}` } : {};
}

export async function apiGet(path, token) {
  const res = await fetch(`${API_BASE}${path}`, { headers: authHeaders(token) });
  if (!res.ok) throw new Error(`GET ${path} failed: ${res.status}`);
  return res.json();
}

export async function apiPost(path, body, token) {
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: { 'Content-Type': 'application/json', ...authHeaders(token) },
    body: JSON.stringify(body),
  });
  let data = {};
  try { data = await res.json(); } catch { /* empty body */ }
  if (!res.ok) throw new Error(data.message || `Request failed (${res.status})`);
  return data;
}

// Upload a file (multipart). Do NOT set Content-Type — the browser adds the
// correct multipart boundary automatically.
export async function apiUpload(path, file, token) {
  const form = new FormData();
  form.append('file', file);
  const res = await fetch(`${API_BASE}${path}`, {
    method: 'POST',
    headers: authHeaders(token),
    body: form,
  });
  if (!res.ok) throw new Error(`Upload failed (${res.status})`);
  return res.json();
}
