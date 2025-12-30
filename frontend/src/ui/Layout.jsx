import React from 'react';
import { Link } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';

export default function Layout({ title, links = [], children }) {
  const { user, logout } = useAuth();

  return (
    <div style={{ fontFamily: 'system-ui, Arial', padding: 16, maxWidth: 1100, margin: '0 auto' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', gap: 12 }}>
        <div>
          <div style={{ fontSize: 20, fontWeight: 700 }}>{title}</div>
          <div style={{ fontSize: 12, opacity: 0.8 }}>{user?.email} ({user?.role})</div>
        </div>
        <button onClick={logout} style={{ padding: '8px 12px', cursor: 'pointer' }}>Logout</button>
      </div>

      <div style={{ marginTop: 12, display: 'flex', gap: 12, flexWrap: 'wrap' }}>
        {links.map((l) => (
          <Link key={l.to} to={l.to} style={{ textDecoration: 'none' }}>{l.label}</Link>
        ))}
      </div>

      <div style={{ marginTop: 16 }}>{children}</div>
    </div>
  );
}
