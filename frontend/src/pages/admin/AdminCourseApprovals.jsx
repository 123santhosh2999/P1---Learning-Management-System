import React, { useEffect, useState } from 'react';
import Layout from '../../ui/Layout.jsx';
import { api } from '../../api/client.js';
import Badge from '../../ui/Badge.jsx';
import Button from '../../ui/Button.jsx';
import Card from '../../ui/Card.jsx';
import EmptyState from '../../ui/EmptyState.jsx';
import Spinner from '../../ui/Spinner.jsx';
import Table from '../../ui/Table.jsx';
import { useToast } from '../../ui/Toast.jsx';

export default function AdminCourseApprovals() {
  const [courses, setCourses] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const [status, setStatus] = useState('');
  const { toast } = useToast();

  async function load() {
    setError(null);
    try {
      setLoading(true);
      const { data } = await api.get('/admin/courses', {
        params: status ? { status } : undefined
      });
      setCourses(data);
    } catch (e) {
      setError(e?.response?.data?.message ?? 'Failed to load courses');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, [status]);

  async function approve(id) {
    try {
      await api.patch(`/admin/courses/${id}/approve`);
      toast({ type: 'success', title: 'Approved', message: 'Course approved.' });
      await load();
    } catch (e) {
      toast({ type: 'error', title: 'Failed', message: e?.response?.data?.message ?? 'Failed to approve' });
    }
  }

  async function reject(id) {
    try {
      await api.patch(`/admin/courses/${id}/reject`);
      toast({ type: 'success', title: 'Rejected', message: 'Course rejected.' });
      await load();
    } catch (e) {
      toast({ type: 'error', title: 'Failed', message: e?.response?.data?.message ?? 'Failed to reject' });
    }
  }

  async function remove(id) {
    if (!confirm('Delete course?')) return;
    try {
      await api.delete(`/admin/courses/${id}`);
      toast({ type: 'success', title: 'Deleted', message: 'Course deleted.' });
      await load();
    } catch (e) {
      toast({ type: 'error', title: 'Failed', message: e?.response?.data?.message ?? 'Failed to delete' });
    }
  }

  function statusTone(s) {
    if (s === 'APPROVED') return 'green';
    if (s === 'PENDING') return 'yellow';
    if (s === 'REJECTED') return 'red';
    return 'gray';
  }

  return (
    <Layout title="Admin - Course Approvals" links={[{ to: '/admin', label: 'Dashboard' }]}
    >
      <Card
        title="Course Approvals"
        subtitle="Approve or reject instructor courses"
        actions={
          <select className="lms-select" value={status} onChange={(e) => setStatus(e.target.value)} style={{ width: 220 }}>
            <option value="">All</option>
            <option value="PENDING">Pending</option>
            <option value="APPROVED">Approved</option>
            <option value="REJECTED">Rejected</option>
          </select>
        }
      >
        {error ? <div className="lms-error" style={{ marginBottom: 12 }}>{error}</div> : null}

        {loading ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: 'var(--muted)' }}>
            <Spinner /> Loading...
          </div>
        ) : courses.length === 0 ? (
          <EmptyState title="No courses" description="No courses found for the selected filter." />
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
                    <Button variant="primary" onClick={() => approve(c.id)}>Approve</Button>
                    <Button onClick={() => reject(c.id)}>Reject</Button>
                    <Button variant="danger" onClick={() => remove(c.id)}>Delete</Button>
                  </div>
                )
              }
            ]}
            data={courses}
          />
        )}
      </Card>
    </Layout>
  );
}
