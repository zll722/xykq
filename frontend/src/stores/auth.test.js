import { beforeEach, describe, expect, it, vi } from 'vitest';
import { createPinia, setActivePinia } from 'pinia';
import { useAuthStore } from './auth';

vi.mock('../api/auth', () => ({
  loginApi: vi.fn(),
  meApi: vi.fn(),
  permissionsApi: vi.fn()
}));

import { loginApi, meApi, permissionsApi } from '../api/auth';

describe('auth store', () => {
  beforeEach(() => {
    setActivePinia(createPinia());
    vi.clearAllMocks();
  });

  it('login should persist token, user and permissions', async () => {
    loginApi.mockResolvedValue({
      token: 't-1',
      userInfo: { id: 1, username: 'u', roleCode: 'ADMIN' },
      permissions: ['user:create']
    });
    const store = useAuthStore();
    await store.login('u', 'p');
    expect(store.token).toBe('t-1');
    expect(store.permissions).toEqual(['user:create']);
    expect(localStorage.getItem('token')).toBe('t-1');
  });

  it('fetchMe should load permissions when empty', async () => {
    meApi.mockResolvedValue({ id: 1, username: 'u', roleCode: 'ADMIN' });
    permissionsApi.mockResolvedValue(['stats:export']);
    const store = useAuthStore();
    store.token = 't-1';
    store.permissions = [];
    await store.fetchMe();
    expect(store.userInfo.username).toBe('u');
    expect(store.permissions).toContain('stats:export');
  });
});
