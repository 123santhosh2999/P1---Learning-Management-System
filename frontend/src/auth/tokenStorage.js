const KEY = 'lms_token';
const USER_KEY = 'lms_user';

export function getToken() {
  return localStorage.getItem(KEY);
}

export function setToken(token) {
  if (!token) localStorage.removeItem(KEY);
  else localStorage.setItem(KEY, token);
}

export function getStoredUser() {
  const raw = localStorage.getItem(USER_KEY);
  if (!raw) return null;
  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function setStoredUser(user) {
  if (!user) localStorage.removeItem(USER_KEY);
  else localStorage.setItem(USER_KEY, JSON.stringify(user));
}
