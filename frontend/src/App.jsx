import React from 'react';
import { Navigate, Route, Routes } from 'react-router-dom';

import { AuthProvider, useAuth } from './auth/AuthContext.jsx';

import LoginPage from './pages/LoginPage.jsx';
import SignupPage from './pages/SignupPage.jsx';
import StudentDashboard from './pages/student/StudentDashboard.jsx';
import BrowseCourses from './pages/student/BrowseCourses.jsx';
import CourseLessons from './pages/student/CourseLessons.jsx';

import InstructorDashboard from './pages/instructor/InstructorDashboard.jsx';
import InstructorCourses from './pages/instructor/InstructorCourses.jsx';
import InstructorCourseDetail from './pages/instructor/InstructorCourseDetail.jsx';

import AdminDashboard from './pages/admin/AdminDashboard.jsx';
import AdminUsers from './pages/admin/AdminUsers.jsx';
import AdminCourseApprovals from './pages/admin/AdminCourseApprovals.jsx';

function RequireAuth({ children }) {
  const { token } = useAuth();
  if (!token) return <Navigate to="/login" replace />;
  return children;
}

function RequireRole({ role, children }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  if (user.role !== role) return <Navigate to="/" replace />;
  return children;
}

function HomeRedirect() {
  const { user, token } = useAuth();
  if (!token) return <Navigate to="/login" replace />;
  if (user?.role === 'ADMIN') return <Navigate to="/admin" replace />;
  if (user?.role === 'INSTRUCTOR') return <Navigate to="/instructor" replace />;
  return <Navigate to="/student" replace />;
}

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/" element={<HomeRedirect />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignupPage />} />

        <Route
          path="/student"
          element={
            <RequireAuth>
              <RequireRole role="STUDENT">
                <StudentDashboard />
              </RequireRole>
            </RequireAuth>
          }
        />
        <Route
          path="/student/courses"
          element={
            <RequireAuth>
              <RequireRole role="STUDENT">
                <BrowseCourses />
              </RequireRole>
            </RequireAuth>
          }
        />
        <Route
          path="/student/courses/:courseId/lessons"
          element={
            <RequireAuth>
              <RequireRole role="STUDENT">
                <CourseLessons />
              </RequireRole>
            </RequireAuth>
          }
        />

        <Route
          path="/instructor"
          element={
            <RequireAuth>
              <RequireRole role="INSTRUCTOR">
                <InstructorDashboard />
              </RequireRole>
            </RequireAuth>
          }
        />
        <Route
          path="/instructor/courses"
          element={
            <RequireAuth>
              <RequireRole role="INSTRUCTOR">
                <InstructorCourses />
              </RequireRole>
            </RequireAuth>
          }
        />
        <Route
          path="/instructor/courses/:courseId"
          element={
            <RequireAuth>
              <RequireRole role="INSTRUCTOR">
                <InstructorCourseDetail />
              </RequireRole>
            </RequireAuth>
          }
        />

        <Route
          path="/admin"
          element={
            <RequireAuth>
              <RequireRole role="ADMIN">
                <AdminDashboard />
              </RequireRole>
            </RequireAuth>
          }
        />
        <Route
          path="/admin/users"
          element={
            <RequireAuth>
              <RequireRole role="ADMIN">
                <AdminUsers />
              </RequireRole>
            </RequireAuth>
          }
        />
        <Route
          path="/admin/courses"
          element={
            <RequireAuth>
              <RequireRole role="ADMIN">
                <AdminCourseApprovals />
              </RequireRole>
            </RequireAuth>
          }
        />

        <Route path="*" element={<Navigate to="/" replace />} />
      </Routes>
    </AuthProvider>
  );
}
