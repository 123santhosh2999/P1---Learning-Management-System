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

export default function AdminUsers() {
  const [users, setUsers] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();

  async function load() {
    setError(null);
    try {
      setLoading(true);
      const { data } = await api.get('/admin/users');
      setUsers(data);
    } catch (e) {
      setError(e?.response?.data?.message ?? 'Failed to load users');
    } finally {
      setLoading(false);
    }
  }

  useEffect(() => {
    load();
  }, []);

  async function setRole(id, role) {
    try {
      await api.patch(`/admin/users/${id}/role`, { role });
      toast({ type: 'success', title: 'Updated', message: `Role updated to ${role}.` });
      await load();
    } catch (e) {
      toast({ type: 'error', title: 'Failed', message: e?.response?.data?.message ?? 'Failed' });
    }
  }

  async function remove(id) {
    if (!confirm('Delete user?')) return;
    try {
      await api.delete(`/admin/users/${id}`);
      toast({ type: 'success', title: 'Deleted', message: 'User deleted.' });
      await load();
    } catch (e) {
      toast({ type: 'error', title: 'Failed', message: e?.response?.data?.message ?? 'Failed' });
    }
  }

  function roleTone(role) {
    if (role === 'ADMIN') return 'red';
    if (role === 'INSTRUCTOR') return 'yellow';
    return 'green';
  }

  return (
    <Layout title="Admin - Users" links={[{ to: '/admin', label: 'Dashboard' }]}
    >
      <Card title="Users" subtitle="Manage roles and accounts">
        {error ? <div className="lms-error" style={{ marginBottom: 12 }}>{error}</div> : null}

        {loading ? (
          <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: 'var(--muted)' }}>
            <Spinner /> Loading...
          </div>
        ) : users.length === 0 ? (
          <EmptyState title="No users" description="No users were found." />
        ) : (
          <Table
            columns={[
              {
                key: 'name',
                header: 'User',
                render: (u) => (
                  <div>
                    <div style={{ fontWeight: 900 }}>{u.name}</div>
                    <div style={{ fontSize: 12, color: 'var(--muted)' }}>{u.email}</div>
                  </div>
                )
              },
              {
                key: 'role',
                header: 'Role',
                render: (u) => <Badge tone={roleTone(u.role)}>{u.role}</Badge>
              },
              {
                key: 'actions',
                header: 'Actions',
                render: (u) => (
                  <div className="lms-row">
                    <Button onClick={() => setRole(u.id, 'STUDENT')}>Student</Button>
                    <Button onClick={() => setRole(u.id, 'INSTRUCTOR')}>Instructor</Button>
                    <Button onClick={() => setRole(u.id, 'ADMIN')}>Admin</Button>
                    <Button variant="danger" onClick={() => remove(u.id)}>Delete</Button>
                  </div>
                )
              }
            ]}
            data={users}
          />
        )}
      </Card>
    </Layout>
  );
}
