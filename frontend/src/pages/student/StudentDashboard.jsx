import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import Layout from '../../ui/Layout.jsx';
import { api } from '../../api/client.js';
import Badge from '../../ui/Badge.jsx';
import Button from '../../ui/Button.jsx';
import Card from '../../ui/Card.jsx';
import EmptyState from '../../ui/EmptyState.jsx';
import Spinner from '../../ui/Spinner.jsx';
import Table from '../../ui/Table.jsx';
import { useToast } from '../../ui/Toast.jsx';

export default function StudentDashboard() {
  const [enrollments, setEnrollments] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();

  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        const { data } = await api.get('/me/enrollments');
        setEnrollments(data);
      } catch (e) {
        setError(e?.response?.data?.message ?? 'Failed to load enrollments');
      } finally {
        setLoading(false);
      }
    })();
  }, []);

  function statusTone(status) {
    if (status === 'APPROVED') return 'green';
    if (status === 'PENDING') return 'yellow';
    if (status === 'REJECTED') return 'red';
    return 'gray';
  }

  async function showProgress(courseId) {
    try {
      const { data } = await api.get('/courses/progress/summary', { params: { courseId } });
      toast({
        type: 'success',
        title: 'Progress',
        message: `Completed ${data.completedLessons} of ${data.totalLessons} lessons.`
      });
    } catch (e) {
      toast({
        type: 'error',
        title: 'Failed',
        message: e?.response?.data?.message ?? 'Failed to load progress'
      });
    }
  }

  return (
    <Layout
      title="Student Dashboard"
      links={[{ to: '/student/courses', label: 'Browse Courses' }]}
    >
      <Card
        title="My Enrollments"
        subtitle="Courses you’re enrolled in"
        actions={
          <Link to="/student/courses">
            <Button variant="primary">Browse Courses</Button>
          </Link>
        }
      >
        {error ? <div className="lms-error" style={{ marginBottom: 12 }}>{error}</div> : null}

        {loading ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: 'var(--muted)' }}>
            <Spinner /> Loading...
          </div>
        ) : enrollments.length === 0 ? (
          <EmptyState
            title="No enrollments yet"
            description="Browse courses and enroll to start learning."
            actionLabel="Browse courses"
            onAction={() => (window.location.href = '/student/courses')}
          />
        ) : (
          <Table
            columns={[
              {
                key: 'courseTitle',
                header: 'Course',
                render: (e) => (
                  <div>
                    <div style={{ fontWeight: 900 }}>{e.courseTitle}</div>
                    <div style={{ fontSize: 12, color: 'var(--muted)' }}>Course ID: {e.courseId}</div>
                  </div>
                )
              },
              {
                key: 'courseStatus',
                header: 'Status',
                render: (e) => <Badge tone={statusTone(e.courseStatus)}>{e.courseStatus}</Badge>
              },
              {
                key: 'actions',
                header: 'Actions',
                render: (e) => (
                  <div className="lms-row">
                    <Link to={`/student/courses/${e.courseId}/lessons`}>
                      <Button>Lessons</Button>
                    </Link>
                    <Button variant="primary" onClick={() => showProgress(e.courseId)}>Progress</Button>
                  </div>
                )
              }
            ]}
            data={enrollments}
          />
        )}
      </Card>
    </Layout>
  );
}
