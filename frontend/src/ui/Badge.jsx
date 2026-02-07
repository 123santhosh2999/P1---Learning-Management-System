import React from 'react';

export default function Badge({ tone = 'gray', children }) {
  return <span className={`lms-badge lms-badge-${tone}`}>{children}</span>;
}
