import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import Layout from '../../ui/Layout.jsx';
import { api } from '../../api/client.js';
import Button from '../../ui/Button.jsx';
import Card from '../../ui/Card.jsx';
import EmptyState from '../../ui/EmptyState.jsx';
import Spinner from '../../ui/Spinner.jsx';
import { useToast } from '../../ui/Toast.jsx';

export default function CourseLessons() {
  const { courseId } = useParams();
  const [lessons, setLessons] = useState([]);
  const [error, setError] = useState(null);
  const [loading, setLoading] = useState(true);
  const { toast } = useToast();

  useEffect(() => {
    (async () => {
      try {
        setLoading(true);
        const { data } = await api.get(`/courses/${courseId}/lessons`);
        setLessons(data);
      } catch (e) {
        setError(e?.response?.data?.message ?? 'Failed to load lessons');
      } finally {
        setLoading(false);
      }
    })();
  }, [courseId]);

  async function setProgress(lessonId, status) {
    try {
      await api.post(`/lessons/${lessonId}/progress`, { status });
      toast({ type: 'success', title: 'Updated', message: 'Progress updated.' });
    } catch (e) {
      toast({ type: 'error', title: 'Failed', message: e?.response?.data?.message ?? 'Failed to update progress' });
    }
  }

  return (
    <Layout title="Course Lessons" links={[{ to: '/student/courses', label: 'Browse Courses' }, { to: '/student', label: 'Dashboard' }]}
    >
      {error ? <div className="lms-error" style={{ marginBottom: 12 }}>{error}</div> : null}

      {loading ? (
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: 'var(--muted)' }}>
          <Spinner /> Loading...
        </div>
      ) : lessons.length === 0 ? (
        <EmptyState title="No lessons" description="This course has no lessons yet." />
      ) : (
        <div className="lms-grid">
          {lessons.map((l) => (
            <Card
              key={l.id}
              title={l.title}
              actions={
                <>
                  <Button onClick={() => setProgress(l.id, 'IN_PROGRESS')}>In Progress</Button>
                  <Button variant="primary" onClick={() => setProgress(l.id, 'COMPLETED')}>Completed</Button>
                </>
              }
            >
              {l.contentText ? <div style={{ marginTop: 8, whiteSpace: 'pre-wrap' }}>{l.contentText}</div> : null}
              <div style={{ marginTop: 10, display: 'grid', gap: 6, fontSize: 13 }}>
                {l.videoUrl ? (
                  <div>
                    Video: <a href={l.videoUrl} target="_blank" rel="noreferrer">Open</a>
                  </div>
                ) : null}
                {l.pdfUrl ? (
                  <div>
                    PDF: <a href={l.pdfUrl} target="_blank" rel="noreferrer">Open</a>
                  </div>
                ) : null}
                {l.mediaPath ? (
                  <div>
                    Media: <a href={`http://localhost:8080${l.mediaPath}`} target="_blank" rel="noreferrer">Download</a>
                  </div>
                ) : null}
              </div>
            </Card>
          ))}
        </div>
      )}
    </Layout>
  );
}
