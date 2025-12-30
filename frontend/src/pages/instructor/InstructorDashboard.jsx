import React from 'react';
import Layout from '../../ui/Layout.jsx';

export default function InstructorDashboard() {
  return (
    <Layout
      title="Instructor Dashboard"
      links={[{ to: '/instructor/courses', label: 'My Courses' }]}
    >
      <div>Create courses and add lessons. Courses require admin approval before students can see them.</div>
    </Layout>
  );
}
