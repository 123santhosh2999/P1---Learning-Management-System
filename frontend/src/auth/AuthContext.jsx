import React, { createContext, useContext, useMemo, useState } from 'react';
import { api } from '../api/client.js';
import { getStoredUser, getToken, setStoredUser, setToken } from './tokenStorage.js';

const AuthContext = createContext(null);

export function AuthProvider({ children }) {
  const [token, setTokenState] = useState(() => getToken());
  const [user, setUserState] = useState(() => getStoredUser());

  async function login(email, password) {
    const { data } = await api.post('/auth/login', { email, password });
    setToken(data.token);
    setStoredUser(data.user);
    setTokenState(data.token);
    setUserState(data.user);
    return data.user;
  }

  async function signup(name, email, password) {
    await api.post('/auth/signup', { name, email, password });
  }

  function logout() {
    setToken(null);
    setStoredUser(null);
    setTokenState(null);
    setUserState(null);
  }

  const value = useMemo(
    () => ({ token, user, login, signup, logout }),
    [token, user]
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
