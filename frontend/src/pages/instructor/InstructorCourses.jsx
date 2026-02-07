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

export default function InstructorCourses() {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [creating, setCreating] = useState(false);

  const [courses, setCourses] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);
  const { toast } = useToast();

  async function load() {
    setError(null);
    try {
      setLoading(true);
      const { data } = await api.get('/instructor/courses');
      setCourses(data);
    } catch (e) {
      setError(e?.response?.data?.message ?? 'Failed to load courses');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  function statusTone(status) {
    if (status === 'APPROVED') return 'green';
    if (status === 'PENDING') return 'yellow';
    if (status === 'REJECTED') return 'red';
    return 'gray';
  }

  async function createCourse(e) {
    e.preventDefault();
    try {
      setCreating(true);
      const { data } = await api.post('/instructor/courses', { title, description });
      setTitle('');
      setDescription('');
      toast({ type: 'success', title: 'Course created', message: 'Course created and sent for approval.' });
      await load();
      toast({ type: 'info', title: 'Next', message: `Open course ${data.id} to add lessons.` });
    } catch (e2) {
      toast({ type: 'error', title: 'Failed', message: e2?.response?.data?.message ?? 'Failed to create course' });
    } finally {
      setCreating(false);
    }
  }

  return (
    <Layout title="Instructor Courses" links={[{ to: '/instructor', label: 'Dashboard' }]}
    >
      <div className="lms-grid">
        <Card title="Create Course" subtitle="New courses require admin approval before students can see them."
        >
          <form onSubmit={createCourse} className="lms-grid" style={{ maxWidth: 720 }}>
            <input className="lms-input" placeholder="Title" value={title} onChange={(e) => setTitle(e.target.value)} />
            <textarea className="lms-textarea" placeholder="Description" value={description} onChange={(e) => setDescription(e.target.value)} rows={4} />
            <div className="lms-row">
              <Button variant="primary" type="submit" disabled={creating || !title.trim()}>
                {creating ? 'Creating...' : 'Create course'}
              </Button>
              <Link to="/instructor">
                <Button>Back</Button>
              </Link>
            </div>
          </form>
        </Card>

        <Card title="My Courses" subtitle="Manage lessons and view enrollments">
          {error ? <div className="lms-error" style={{ marginBottom: 12 }}>{error}</div> : null}

          {loading ? (
            <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: 'var(--muted)' }}>
              <Spinner /> Loading...
            </div>
          ) : courses.length === 0 ? (
            <EmptyState title="No courses yet" description="Create your first course to get started." />
          ) : (
            <Table
              columns={[
                {
                  key: 'title',
                  header: 'Course',
                  render: (c) => (
                    <div>
                      <div style={{ fontWeight: 900 }}>{c.title}</div>
                      <div style={{ fontSize: 12, color: 'var(--muted)' }}>{c.description || '—'}</div>
                    </div>
                  )
                },
                {
                  key: 'status',
                  header: 'Status',
                  render: (c) => <Badge tone={statusTone(c.status)}>{c.status}</Badge>
                },
                {
                  key: 'actions',
                  header: 'Actions',
                  render: (c) => (
                    <div className="lms-row">
                      <Link to={`/instructor/courses/${c.id}`}>
                        <Button variant="primary">Manage</Button>
                      </Link>
                    </div>
                  )
                }
              ]}
              data={courses}
            />
          )}
        </Card>
      </div>
    </Layout>
  );
}
