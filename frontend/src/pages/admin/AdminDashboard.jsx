import React from 'react';
import Layout from '../../ui/Layout.jsx';

export default function AdminDashboard() {
  return (
    <Layout
      title="Admin Dashboard"
      links={[
        { to: '/admin/users', label: 'Manage Users' },
        { to: '/admin/courses', label: 'Course Approvals' }
      ]}
    >
      <div>Use the links above to manage users and approve courses.</div>
    </Layout>
  );
}
