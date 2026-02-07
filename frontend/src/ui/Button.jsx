import React from 'react';

export default function Button({ variant = 'default', className = '', type = 'button', ...props }) {
  const base = 'lms-btn';
  const v =
    variant === 'primary'
      ? 'lms-btn-primary'
      : variant === 'danger'
      ? 'lms-btn-danger'
      : '';

  return <button type={type} className={`${base} ${v} ${className}`.trim()} {...props} />;
}
