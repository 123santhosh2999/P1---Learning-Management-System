import React, { useState } from 'react';
import { useParams } from 'react-router-dom';
import Layout from '../../ui/Layout.jsx';
import { api } from '../../api/client.js';

export default function InstructorCourseDetail() {
  const { courseId } = useParams();

  const [title, setTitle] = useState('');
  const [contentText, setContentText] = useState('');
  const [videoUrl, setVideoUrl] = useState('');
  const [pdfUrl, setPdfUrl] = useState('');
  const [media, setMedia] = useState(null);
  const [result, setResult] = useState(null);

  async function addLesson(e) {
    e.preventDefault();
    try {
      const form = new FormData();
      form.append(
        'data',
        new Blob(
          [JSON.stringify({ title, contentText, videoUrl: videoUrl || null, pdfUrl: pdfUrl || null, orderIndex: 0 })],
          { type: 'application/json' }
        )
      );
      if (media) form.append('media', media);

      const { data } = await api.post(`/instructor/courses/${courseId}/lessons`, form, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      setResult(data);
      setTitle('');
      setContentText('');
      setVideoUrl('');
      setPdfUrl('');
      setMedia(null);
    } catch (e2) {
      alert(e2?.response?.data?.message ?? 'Failed to add lesson');
    }
  }

  return (
    <Layout
      title={`Instructor - Course ${courseId}`}
      links={[{ to: '/instructor', label: 'Dashboard' }, { to: '/instructor/courses', label: 'Create Course' }]}
    >
      <h3>Add Lesson</h3>
      <form onSubmit={addLesson} style={{ display: 'grid', gap: 10, maxWidth: 700 }}>
        <input placeholder="Lesson Title" value={title} onChange={(e) => setTitle(e.target.value)} />
        <textarea rows={5} placeholder="Text Content" value={contentText} onChange={(e) => setContentText(e.target.value)} />
        <input placeholder="Video URL (optional)" value={videoUrl} onChange={(e) => setVideoUrl(e.target.value)} />
        <input placeholder="PDF URL (optional)" value={pdfUrl} onChange={(e) => setPdfUrl(e.target.value)} />
        <input type="file" onChange={(e) => setMedia(e.target.files?.[0] ?? null)} />
        <button type="submit">Add Lesson</button>
      </form>

      {result ? (
        <div style={{ marginTop: 16 }}>
          <div style={{ fontWeight: 700 }}>Created Lesson</div>
          <pre style={{ background: '#f5f5f5', padding: 12, overflow: 'auto' }}>{JSON.stringify(result, null, 2)}</pre>
        </div>
      ) : null}
    </Layout>
  );
}
