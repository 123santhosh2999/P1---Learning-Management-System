import React, { useEffect, useMemo, useState } from 'react';
import { Link, useParams } from 'react-router-dom';
import Layout from '../../ui/Layout.jsx';
import { api } from '../../api/client.js';
import Badge from '../../ui/Badge.jsx';
import Button from '../../ui/Button.jsx';
import Card from '../../ui/Card.jsx';
import EmptyState from '../../ui/EmptyState.jsx';
import Spinner from '../../ui/Spinner.jsx';
import Table from '../../ui/Table.jsx';
import { useToast } from '../../ui/Toast.jsx';

export default function InstructorCourseDetail() {
  const { courseId } = useParams();

  const [course, setCourse] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  const [lessons, setLessons] = useState([]);
  const [lessonsLoading, setLessonsLoading] = useState(true);

  const [enrollments, setEnrollments] = useState([]);
  const [enrollmentsLoading, setEnrollmentsLoading] = useState(true);

  const { toast } = useToast();

  const [title, setTitle] = useState('');
  const [contentText, setContentText] = useState('');
  const [videoUrl, setVideoUrl] = useState('');
  const [pdfUrl, setPdfUrl] = useState('');
  const [media, setMedia] = useState(null);

  const [editTitle, setEditTitle] = useState('');
  const [editDescription, setEditDescription] = useState('');
  const [savingCourse, setSavingCourse] = useState(false);

  const [addingLesson, setAddingLesson] = useState(false);

  const courseIdNum = useMemo(() => Number(courseId), [courseId]);

  function statusTone(status) {
    if (status === 'APPROVED') return 'green';
    if (status === 'PENDING') return 'yellow';
    if (status === 'REJECTED') return 'red';
    return 'gray';
  }

  async function loadCourse() {
    setError(null);
    try {
      setLoading(true);
      const { data } = await api.get('/instructor/courses');
      const found = (data ?? []).find((c) => c.id === courseIdNum);
      if (!found) throw new Error('Course not found');
      setCourse(found);
      setEditTitle(found.title ?? '');
      setEditDescription(found.description ?? '');
    } catch (e) {
      setError(e?.response?.data?.message ?? e?.message ?? 'Failed to load course');
    } finally {
      setLoading(false);
    }
  }

  async function loadLessons() {
    try {
      setLessonsLoading(true);
      const { data } = await api.get(`/instructor/courses/${courseId}/lessons`);
      setLessons(data);
    } catch (e) {
      toast({ type: 'error', title: 'Failed', message: e?.response?.data?.message ?? 'Failed to load lessons' });
    } finally {
      setLessonsLoading(false);
    }
  }

  async function loadEnrollments() {
    try {
      setEnrollmentsLoading(true);
      const { data } = await api.get(`/instructor/courses/${courseId}/enrollments`);
      setEnrollments(data);
    } catch (e) {
      toast({ type: 'error', title: 'Failed', message: e?.response?.data?.message ?? 'Failed to load enrollments' });
    } finally {
      setEnrollmentsLoading(false);
    }
  }

  useEffect(() => {
    loadCourse();
    loadLessons();
    loadEnrollments();
  }, [courseId]);

  async function saveCourse(e) {
    e.preventDefault();
    try {
      setSavingCourse(true);
      const { data } = await api.put(`/instructor/courses/${courseId}`, {
        title: editTitle,
        description: editDescription
      });
      setCourse(data);
      toast({ type: 'success', title: 'Saved', message: 'Course updated (status reset to PENDING for approval).' });
    } catch (e2) {
      toast({ type: 'error', title: 'Failed', message: e2?.response?.data?.message ?? 'Failed to update course' });
    } finally {
      setSavingCourse(false);
    }
  }

  async function addLesson(e) {
    e.preventDefault();
    try {
      setAddingLesson(true);
      const form = new FormData();
      form.append(
        'data',
        new Blob(
          [JSON.stringify({ title, contentText, videoUrl: videoUrl || null, pdfUrl: pdfUrl || null, orderIndex: 0 })],
          { type: 'application/json' }
        )
      );
      if (media) form.append('media', media);

      await api.post(`/instructor/courses/${courseId}/lessons`, form, {
        headers: { 'Content-Type': 'multipart/form-data' }
      });
      setTitle('');
      setContentText('');
      setVideoUrl('');
      setPdfUrl('');
      setMedia(null);
      toast({ type: 'success', title: 'Lesson added', message: 'Lesson created successfully.' });
      await loadLessons();
    } catch (e2) {
      toast({ type: 'error', title: 'Failed', message: e2?.response?.data?.message ?? 'Failed to add lesson' });
    } finally {
      setAddingLesson(false);
    }
  }

  return (
    <Layout
      title={`Instructor Course #${courseId}`}
      links={[{ to: '/instructor', label: 'Dashboard' }, { to: '/instructor/courses', label: 'My Courses' }]}
    >
      {error ? <div className="lms-error" style={{ marginBottom: 12 }}>{error}</div> : null}

      {loading ? (
        <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: 'var(--muted)' }}>
          <Spinner /> Loading...
        </div>
      ) : !course ? (
        <EmptyState title="Course not found" description="Go back to your courses list." actionLabel="My Courses" onAction={() => (window.location.href = '/instructor/courses')} />
      ) : (
        <div className="lms-grid">
          <Card
            title={course.title}
            subtitle={course.description || '—'}
            actions={
              <>
                <Badge tone={statusTone(course.status)}>{course.status}</Badge>
                <Link to="/instructor/courses">
                  <Button>All courses</Button>
                </Link>
              </>
            }
          >
            <div style={{ fontSize: 12, color: 'var(--muted)' }}>Course ID: {course.id}</div>
          </Card>

          <Card title="Edit Course" subtitle="Editing resets approval status to PENDING">
            <form onSubmit={saveCourse} className="lms-grid" style={{ maxWidth: 860 }}>
              <input className="lms-input" value={editTitle} onChange={(e) => setEditTitle(e.target.value)} placeholder="Title" />
              <textarea className="lms-textarea" value={editDescription} onChange={(e) => setEditDescription(e.target.value)} placeholder="Description" rows={4} />
              <div className="lms-row">
                <Button variant="primary" type="submit" disabled={savingCourse || !editTitle.trim()}>
                  {savingCourse ? 'Saving...' : 'Save changes'}
                </Button>
              </div>
            </form>
          </Card>

          <Card title="Lessons" subtitle="Content for this course">
            {lessonsLoading ? (
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: 'var(--muted)' }}>
                <Spinner /> Loading lessons...
              </div>
            ) : lessons.length === 0 ? (
              <EmptyState title="No lessons yet" description="Add your first lesson below." />
            ) : (
              <Table
                columns={[
                  {
                    key: 'title',
                    header: 'Lesson',
                    render: (l) => (
                      <div>
                        <div style={{ fontWeight: 900 }}>{l.title}</div>
                        <div style={{ fontSize: 12, color: 'var(--muted)' }}>Order: {l.orderIndex ?? 0}</div>
                      </div>
                    )
                  },
                  {
                    key: 'resources',
                    header: 'Resources',
                    render: (l) => (
                      <div style={{ display: 'grid', gap: 4, fontSize: 13 }}>
                        {l.videoUrl ? <a href={l.videoUrl} target="_blank" rel="noreferrer">Video</a> : <span style={{ color: 'var(--muted)' }}>—</span>}
                        {l.pdfUrl ? <a href={l.pdfUrl} target="_blank" rel="noreferrer">PDF</a> : null}
                        {l.mediaPath ? <a href={`http://localhost:8080${l.mediaPath}`} target="_blank" rel="noreferrer">Media</a> : null}
                      </div>
                    )
                  }
                ]}
                data={lessons}
              />
            )}
          </Card>

          <Card title="Add Lesson" subtitle="Upload optional media or link a video/PDF">
            <form onSubmit={addLesson} className="lms-grid" style={{ maxWidth: 980 }}>
              <input className="lms-input" placeholder="Lesson Title" value={title} onChange={(e) => setTitle(e.target.value)} />
              <textarea className="lms-textarea" rows={5} placeholder="Text Content" value={contentText} onChange={(e) => setContentText(e.target.value)} />
              <div className="lms-row">
                <input className="lms-input" placeholder="Video URL (optional)" value={videoUrl} onChange={(e) => setVideoUrl(e.target.value)} />
                <input className="lms-input" placeholder="PDF URL (optional)" value={pdfUrl} onChange={(e) => setPdfUrl(e.target.value)} />
              </div>
              <input className="lms-input" type="file" onChange={(e) => setMedia(e.target.files?.[0] ?? null)} />
              <div className="lms-row">
                <Button variant="primary" type="submit" disabled={addingLesson || !title.trim()}>
                  {addingLesson ? 'Adding...' : 'Add lesson'}
                </Button>
              </div>
            </form>
          </Card>

          <Card title="Enrollments" subtitle="Students enrolled in this course">
            {enrollmentsLoading ? (
              <div style={{ display: 'flex', alignItems: 'center', gap: 10, color: 'var(--muted)' }}>
                <Spinner /> Loading enrollments...
              </div>
            ) : enrollments.length === 0 ? (
              <EmptyState title="No enrollments yet" description="Enrollments will appear when students enroll." />
            ) : (
              <Table
                columns={[
                  {
                    key: 'student',
                    header: 'Student',
                    render: (e) => (
                      <div>
                        <div style={{ fontWeight: 900 }}>{e.studentName}</div>
                        <div style={{ fontSize: 12, color: 'var(--muted)' }}>{e.studentEmail}</div>
                      </div>
                    )
                  },
                  { key: 'studentId', header: 'Student ID' },
                  { key: 'id', header: 'Enrollment ID' }
                ]}
                data={enrollments}
              />
            )}
          </Card>
        </div>
      )}
    </Layout>
  );
}
