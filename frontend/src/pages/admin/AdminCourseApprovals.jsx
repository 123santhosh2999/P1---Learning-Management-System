import React, { useEffect, useState } from 'react';
import Layout from '../../ui/Layout.jsx';
import { api } from '../../api/client.js';

export default function AdminCourseApprovals() {
  const [courses, setCourses] = useState([]);
  const [error, setError] = useState(null);

  async function load() {
    setError(null);
    try {
      const { data } = await api.get('/admin/courses');
      setCourses(data);
    } catch (e) {
      setError(e?.response?.data?.message ?? 'Failed to load courses');
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function approve(id) {
    await api.patch(`/admin/courses/${id}/approve`);
    await load();
  }

  async function reject(id) {
    await api.patch(`/admin/courses/${id}/reject`);
    await load();
  }

  async function remove(id) {
    if (!confirm('Delete course?')) return;
    await api.delete(`/admin/courses/${id}`);
    await load();
  }

  return (
    <Layout title="Admin - Course Approvals" links={[{ to: '/admin', label: 'Dashboard' }]}
    >
      {error ? <div style={{ color: 'crimson' }}>{error}</div> : null}
      <div style={{ display: 'grid', gap: 10 }}>
        {courses.map((c) => (
          <div key={c.id} style={{ border: '1px solid #ddd', padding: 12, borderRadius: 8 }}>
            <div style={{ fontWeight: 700 }}>{c.title}</div>
            <div style={{ opacity: 0.8, marginTop: 4 }}>{c.description}</div>
            <div style={{ marginTop: 6 }}>Status: <b>{c.status}</b></div>
            <div style={{ marginTop: 10, display: 'flex', gap: 8, flexWrap: 'wrap' }}>
              <button onClick={() => approve(c.id)}>Approve</button>
              <button onClick={() => reject(c.id)}>Reject</button>
              <button onClick={() => remove(c.id)} style={{ color: 'crimson' }}>Delete</button>
            </div>
          </div>
        ))}
      </div>
    </Layout>
  );
}
