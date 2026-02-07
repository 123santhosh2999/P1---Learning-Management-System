import React from 'react';

export default function Spinner({ size = 18 }) {
  return <span className="lms-spinner" style={{ width: size, height: size }} />;
}
