import React, { useState } from 'react';
import Layout from '../../ui/Layout.jsx';
import { api } from '../../api/client.js';

export default function InstructorCourses() {
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [result, setResult] = useState(null);

  async function createCourse(e) {
    e.preventDefault();
    try {
      const { data } = await api.post('/instructor/courses', { title, description });
      setResult(data);
      setTitle('');
      setDescription('');
    } catch (e2) {
      alert(e2?.response?.data?.message ?? 'Failed to create course');
    }
  }

  return (
    <Layout title="Instructor - Courses" links={[{ to: '/instructor', label: 'Dashboard' }]}
    >
      <h3>Create Course</h3>
      <form onSubmit={createCourse} style={{ display: 'grid', gap: 10, maxWidth: 520 }}>
        <input placeholder="Title" value={title} onChange={(e) => setTitle(e.target.value)} />
        <textarea placeholder="Description" value={description} onChange={(e) => setDescription(e.target.value)} rows={4} />
        <button type="submit">Create (Pending Approval)</button>
      </form>
      {result ? (
        <div style={{ marginTop: 16 }}>
          <div style={{ fontWeight: 700 }}>Created</div>
          <pre style={{ background: '#f5f5f5', padding: 12, overflow: 'auto' }}>{JSON.stringify(result, null, 2)}</pre>
          <div style={{ fontSize: 12, opacity: 0.8 }}>
            Copy the course id and open: <code>/instructor/courses/&lt;id&gt;</code>
          </div>
        </div>
      ) : null}
    </Layout>
  );
}
