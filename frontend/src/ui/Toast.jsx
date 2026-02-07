import React, { createContext, useCallback, useContext, useMemo, useState } from 'react';

const ToastContext = createContext(null);

export function ToastProvider({ children }) {
  const [toasts, setToasts] = useState([]);

  const dismiss = useCallback((id) => {
    setToasts((prev) => prev.filter((t) => t.id !== id));
  }, []);

  const toast = useCallback((input) => {
    const id = `${Date.now()}-${Math.random().toString(16).slice(2)}`;
    const t = {
      id,
      type: input?.type ?? 'info',
      title: input?.title ?? 'Notice',
      message: input?.message ?? '',
      timeoutMs: input?.timeoutMs ?? 3000
    };

    setToasts((prev) => [t, ...prev].slice(0, 5));

    window.setTimeout(() => {
      dismiss(id);
    }, t.timeoutMs);

    return id;
  }, [dismiss]);

  const value = useMemo(() => ({ toast, dismiss }), [toast, dismiss]);

  return (
    <ToastContext.Provider value={value}>
      {children}
      <div className="lms-toast-viewport" role="region" aria-label="Notifications">
        {toasts.map((t) => (
          <div key={t.id} className={`lms-toast lms-toast-${t.type}`.trim()}>
            <div style={{ display: 'flex', justifyContent: 'space-between', gap: 12 }}>
              <div style={{ fontWeight: 900 }}>{t.title}</div>
              <button className="lms-toast-x" onClick={() => dismiss(t.id)} aria-label="Dismiss">×</button>
            </div>
            {t.message ? <div style={{ marginTop: 6, color: 'var(--muted)', fontSize: 13 }}>{t.message}</div> : null}
          </div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error('useToast must be used within ToastProvider');
  return ctx;
}
