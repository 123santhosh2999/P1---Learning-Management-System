import React, { useEffect, useState } from 'react';
import Layout from '../../ui/Layout.jsx';
import { api } from '../../api/client.js';

export default function StudentDashboard() {
  const [enrollments, setEnrollments] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    (async () => {
      try {
        const { data } = await api.get('/me/enrollments');
        setEnrollments(data);
      } catch (e) {
        setError(e?.response?.data?.message ?? 'Failed to load enrollments');
      }
    })();
  }, []);

  return (
    <Layout
      title="Student Dashboard"
      links={[{ to: '/student/courses', label: 'Browse Courses' }]}
    >
      {error ? <div style={{ color: 'crimson' }}>{error}</div> : null}
      <h3>My Enrollments</h3>
      <pre style={{ background: '#f5f5f5', padding: 12, overflow: 'auto' }}>{JSON.stringify(enrollments, null, 2)}</pre>
    </Layout>
  );
}
