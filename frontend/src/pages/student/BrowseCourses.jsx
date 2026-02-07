import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../../ui/Layout.jsx';
import { api } from '../../api/client.js';
import Button from '../../ui/Button.jsx';
import Card from '../../ui/Card.jsx';
import EmptyState from '../../ui/EmptyState.jsx';
import Spinner from '../../ui/Spinner.jsx';
import { useToast } from '../../ui/Toast.jsx';

export default function BrowseCourses() {
  const [courses, setCourses] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();

  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        const { data } = await api.get('/courses');
        setCourses(data);
      } catch (e) {
        setError(e?.response?.data?.message ?? 'Failed to load courses');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  async function enroll(id) {
    try {
      await api.post(`/courses/${id}/enroll`);
      toast({ type: 'success', title: 'Enrolled', message: 'You have been enrolled successfully.' });
    } catch (e) {
      toast({ type: 'error', title: 'Enroll failed', message: e?.response?.data?.message ?? 'Enroll failed' });
    }
  }

  return (
    <Layout
      title="Browse Courses"
      links={[{ to: '/student', label: 'Dashboard' }]}
    >
      {error ? <div className="lms-error" style={{ marginBottom: 12 }}>{error}</div> : null}

      {loading ? (
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: 'var(--muted)' }}>
          <Spinner /> Loading...
        </div>
      ) : courses.length === 0 ? (
        <EmptyState
          title="No courses found"
          description="Courses will appear here after an instructor creates them and an admin approves them."
        />
      ) : (
        <div className="lms-grid">
          {courses.map((c) => (
            <Card
              key={c.id}
              title={c.title}
              subtitle={c.description || '—'}
              actions={
                <>
                  <Button variant="primary" onClick={() => enroll(c.id)}>Enroll</Button>
                  <Link to={`/student/courses/${c.id}/lessons`}>
                    <Button>Lessons</Button>
                  </Link>
                </>
              }
            >
              <div style={{ fontSize: 12, color: 'var(--muted)' }}>Course ID: {c.id}</div>
            </Card>
          ))}
        </div>
      )}
    </Layout>
  );
}
