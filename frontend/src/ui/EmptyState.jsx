import React from 'react';
import Button from './Button.jsx';

export default function EmptyState({ title = 'Nothing here yet', description, actionLabel, onAction }) {
  return (
    <div className="lms-empty">
      <div style={{ fontWeight: 900 }}>{title}</div>
      {description ? <div style={{ marginTop: 6, color: 'var(--muted)', fontSize: 13 }}>{description}</div> : null}
      {actionLabel && onAction ? (
        <div style={{ marginTop: 12 }}>
          <Button variant="primary" onClick={onAction}>{actionLabel}</Button>
        </div>
      ) : null}
    </div>
  );
}
