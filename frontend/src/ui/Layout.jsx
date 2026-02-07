import React from 'react';
import { NavLink } from 'react-router-dom';
import { useAuth } from '../auth/AuthContext.jsx';

export default function Layout({ title, links = [], children }) {
  const { user, logout } = useAuth();

  const role = user?.role;
  const nav = [];
  if (role === 'STUDENT') {
    nav.push(
      { to: '/student', label: 'Dashboard' },
      { to: '/student/courses', label: 'Browse Courses' }
    );
  }
  if (role === 'INSTRUCTOR') {
    nav.push(
      { to: '/instructor', label: 'Dashboard' },
      { to: '/instructor/courses', label: 'My Courses' }
    );
  }
  if (role === 'ADMIN') {
    nav.push(
      { to: '/admin', label: 'Dashboard' },
      { to: '/admin/users', label: 'Users' },
      { to: '/admin/courses', label: 'Course Approvals' }
    );
  }

  return (
    <div className="lms-shell">
      <aside className="lms-sidebar">
        <div className="lms-brand">
          <div className="lms-brand-title">LMS</div>
          <div className="lms-brand-subtitle">Learning Management System</div>
        </div>

        <nav className="lms-nav">
          {nav.map((item) => (
            <NavLink
              key={item.to}
              to={item.to}
              className={({ isActive }) => (isActive ? 'active' : undefined)}
            >
              {item.label}
            </NavLink>
          ))}
        </nav>
      </aside>

      <main className="lms-main">
        <div className="lms-topbar">
          <div>
            <div className="lms-title">{title}</div>
            <div className="lms-meta">{user?.email} ({user?.role})</div>
          </div>
          <div className="lms-actions">
            <button onClick={logout} className="lms-btn">Logout</button>
          </div>
        </div>

        {links.length ? (
          <div className="lms-row" style={{ marginTop: 12 }}>
            {links.map((l) => (
              <NavLink key={l.to} to={l.to}>
                {l.label}
              </NavLink>
            ))}
          </div>
        ) : null}

        <div className="lms-content">{children}</div>
      </main>
    </div>
  );
}
