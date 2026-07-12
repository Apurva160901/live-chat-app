/**
 * Shows a user's profile picture if they have one, otherwise a colored circle
 * with their first initial.
 */
export default function Avatar({ name, url, size = 40 }) {
  const style = { width: size, height: size };
  if (url) {
    return <img className="avatar avatar-img" src={url} alt={name} style={style} />;
  }
  const initial = (name || '?').charAt(0).toUpperCase();
  return <div className="avatar" style={{ ...style, fontSize: size * 0.4 }}>{initial}</div>;
}
