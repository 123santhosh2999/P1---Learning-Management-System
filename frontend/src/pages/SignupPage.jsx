import React, { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';

export default function SignupPage() {
  const { signup } = useAuth();
  const navigate = useNavigate();

  const [name, setName] = useState('');
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');
  const [role, setRole] = useState('STUDENT');
  const [error, setError] = useState(null);

  async function onSubmit(e) {
    e.preventDefault();
    setError(null);
    try {
      await signup(name, email, password, role);
      navigate('/login');
    } catch (err) {
      setError(err?.response?.data?.message ?? 'Signup failed');
    }
  }

  return (
    <div style={{ padding: 20, maxWidth: 560, margin: '28px auto' }}>
      <div className="lms-card">
        <h2 style={{ marginTop: 0 }}>Create account</h2>
        {error ? <div className="lms-error" style={{ marginBottom: 10 }}>{error}</div> : null}
        <form onSubmit={onSubmit} className="lms-grid">
          <input className="lms-input" placeholder="Name" value={name} onChange={(e) => setName(e.target.value)} />
          <input className="lms-input" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
          <input className="lms-input" placeholder="Password" type="password" value={password} onChange={(e) => setPassword(e.target.value)} />
          <select className="lms-select" value={role} onChange={(e) => setRole(e.target.value)}>
            <option value="STUDENT">Student</option>
            <option value="INSTRUCTOR">Instructor</option>
            <option value="ADMIN">Admin</option>
          </select>
          <button className="lms-btn lms-btn-primary" type="submit">Create account</button>
        </form>
        <div style={{ marginTop: 12 }}>
          Already have an account? <Link to="/login">Login</Link>
        </div>
      </div>
    </div>
  );
}
