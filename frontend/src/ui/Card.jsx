import React from 'react';

export default function Card({ title, subtitle, actions, children, className = '' }) {
  return (
    <div className={`lms-card ${className}`.trim()}>
      {title || subtitle || actions ? (
        <div style={{ display: 'flex', alignItems: 'flex-start', justifyContent: 'space-between', gap: 12, marginBottom: 12 }}>
          <div>
            {title ? <div style={{ fontWeight: 900, fontSize: 16 }}>{title}</div> : null}
            {subtitle ? <div style={{ marginTop: 2, color: 'var(--muted)', fontSize: 12 }}>{subtitle}</div> : null}
          </div>
          {actions ? <div style={{ display: 'flex', gap: 10, flexWrap: 'wrap' }}>{actions}</div> : null}
        </div>
      ) : null}
      {children}
    </div>
  );
}
