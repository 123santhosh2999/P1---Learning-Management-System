import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../../ui/Layout.jsx';
import { api } from '../../api/client.js';

export default function BrowseCourses() {
  const [courses, setCourses] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    (async () => {
      try {
        const { data } = await api.get('/courses');
        setCourses(data);
      } catch (e) {
        setError(e?.response?.data?.message ?? 'Failed to load courses');
      }
    })();
  }, []);

  async function enroll(id) {
    try {
      await api.post(`/courses/${id}/enroll`);
      alert('Enrolled');
    } catch (e) {
      alert(e?.response?.data?.message ?? 'Enroll failed');
    }
  }

  return (
    <Layout
      title="Browse Courses"
      links={[{ to: '/student', label: 'Dashboard' }]}
    >
      {error ? <div style={{ color: 'crimson' }}>{error}</div> : null}
      <div style={{ display: 'grid', gap: 12 }}>
        {courses.map((c) => (
          <div key={c.id} style={{ border: '1px solid #ddd', padding: 12, borderRadius: 8 }}>
            <div style={{ fontWeight: 700 }}>{c.title}</div>
            <div style={{ opacity: 0.8, marginTop: 4 }}>{c.description}</div>
            <div style={{ marginTop: 8, display: 'flex', gap: 12 }}>
              <button onClick={() => enroll(c.id)}>Enroll</button>
              <Link to={`/student/courses/${c.id}/lessons`}>View Lessons</Link>
            </div>
          </div>
        ))}
      </div>
    </Layout>
  );
}
