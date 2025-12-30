import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState(null);

  async function onSubmit(e) {
    e.preventDefault();
    setError(null);
    try {
      const user = await login(email, password);
      if (user.role === 'ADMIN') navigate('/admin');
      else if (user.role === 'INSTRUCTOR') navigate('/instructor');
      else navigate('/student');
    } catch (err) {
      setError(err?.response?.data?.message ?? 'Login failed');
    }
  }

  return (
    <div style={{ fontFamily: 'system-ui, Arial', padding: 16, maxWidth: 520, margin: '40px auto' }}>
      <h2>Login</h2>
      {error ? <div style={{ color: 'crimson', marginBottom: 8 }}>{error}</div> : null}
      <form onSubmit={onSubmit} style={{ display: 'grid', gap: 10 }}>
        <input placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
        <input placeholder="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
        <button type="submit">Login</button>
      </form>
      <div style={{ marginTop: 12 }}>
        No account? <Link to="/signup">Sign up</Link>
      </div>
      <div style={{ marginTop: 12, fontSize: 12, opacity: 0.8 }}>
        Note: The very first signup becomes ADMIN automatically.
      </div>
    </div>
  );
}
