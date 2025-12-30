import React, { useEffect, useState } from 'react';
import Layout from '../../ui/Layout.jsx';
import { api } from '../../api/client.js';

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState(null);

  async function load() {
    setError(null);
    try {
      const { data } = await api.get('/admin/users');
      setUsers(data);
    } catch (e) {
      setError(e?.response?.data?.message ?? 'Failed to load users');
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function setRole(id, role) {
    try {
      await api.patch(`/admin/users/${id}/role`, { role });
      await load();
    } catch (e) {
      alert(e?.response?.data?.message ?? 'Failed');
    }
  }

  async function remove(id) {
    if (!confirm('Delete user?')) return;
    try {
      await api.delete(`/admin/users/${id}`);
      await load();
    } catch (e) {
      alert(e?.response?.data?.message ?? 'Failed');
    }
  }

  return (
    <Layout title="Admin - Users" links={[{ to: '/admin', label: 'Dashboard' }]}
    >
      {error ? <div style={{ color: 'crimson' }}>{error}</div> : null}
      <div style={{ display: 'grid', gap: 10 }}>
        {users.map((u) => (
          <div key={u.id} style={{ border: '1px solid #ddd', padding: 12, borderRadius: 8 }}>
            <div style={{ fontWeight: 700 }}>{u.name} ({u.email})</div>
            <div style={{ marginTop: 6 }}>Role: <b>{u.role}</b></div>
            <div style={{ marginTop: 10, display: 'flex', gap: 8, flexWrap: 'wrap' }}>
              <button onClick={() => setRole(u.id, 'STUDENT')}>Make Student</button>
              <button onClick={() => setRole(u.id, 'INSTRUCTOR')}>Make Instructor</button>
              <button onClick={() => setRole(u.id, 'ADMIN')}>Make Admin</button>
              <button onClick={() => remove(u.id)} style={{ color: 'crimson' }}>Delete</button>
            </div>
          </div>
        ))}
      </div>
    </Layout>
  );
}
