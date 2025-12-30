import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Layout from '../../ui/Layout.jsx';
import { api } from '../../api/client.js';

export default function CourseLessons() {
  const { courseId } = useParams();
  const [lessons, setLessons] = useState([]);
  const [error, setError] = useState(null);

  useEffect(() => {
    (async () => {
      try {
        const { data } = await api.get(`/courses/${courseId}/lessons`);
        setLessons(data);
      } catch (e) {
        setError(e?.response?.data?.message ?? 'Failed to load lessons');
      }
    })();
  }, [courseId]);

  async function setProgress(lessonId, status) {
    try {
      await api.post(`/lessons/${lessonId}/progress`, { status });
      alert('Updated');
    } catch (e) {
      alert(e?.response?.data?.message ?? 'Failed to update progress');
    }
  }

  return (
    <Layout title="Course Lessons" links={[{ to: '/student/courses', label: 'Browse Courses' }, { to: '/student', label: 'Dashboard' }]}
    >
      {error ? <div style={{ color: 'crimson' }}>{error}</div> : null}
      <div style={{ display: 'grid', gap: 12 }}>
        {lessons.map((l) => (
          <div key={l.id} style={{ border: '1px solid #ddd', padding: 12, borderRadius: 8 }}>
            <div style={{ fontWeight: 700 }}>{l.title}</div>
            {l.contentText ? <div style={{ marginTop: 8, whiteSpace: 'pre-wrap' }}>{l.contentText}</div> : null}
            {l.videoUrl ? (
              <div style={{ marginTop: 8 }}>
                Video: <a href={l.videoUrl} target="_blank" rel="noreferrer">{l.videoUrl}</a>
              </div>
            ) : null}
            {l.pdfUrl ? (
              <div style={{ marginTop: 8 }}>
                PDF: <a href={l.pdfUrl} target="_blank" rel="noreferrer">{l.pdfUrl}</a>
              </div>
            ) : null}
            {l.mediaPath ? (
              <div style={{ marginTop: 8 }}>
                Media: <a href={`http://localhost:4000${l.mediaPath}`} target="_blank" rel="noreferrer">Download</a>
              </div>
            ) : null}
            <div style={{ marginTop: 10, display: 'flex', gap: 8 }}>
              <button onClick={() => setProgress(l.id, 'IN_PROGRESS')}>Mark In Progress</button>
              <button onClick={() => setProgress(l.id, 'COMPLETED')}>Mark Completed</button>
            </div>
          </div>
        ))}
      </div>
    </Layout>
  );
}
