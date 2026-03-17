import { describe, expect, it, vi } from 'vitest';
import { permissionDirective } from './permission';

vi.mock('../stores/auth', () => ({
  useAuthStore: () => ({
    hasPermission: (code) => code === 'allowed:perm'
  })
}));

describe('permission directive', () => {
  it('should remove element when no permission', () => {
    const parent = document.createElement('div');
    const child = document.createElement('button');
    parent.appendChild(child);
    permissionDirective.mounted(child, { value: 'denied:perm' });
    expect(parent.contains(child)).toBe(false);
  });

  it('should keep element when permission granted', () => {
    const parent = document.createElement('div');
    const child = document.createElement('button');
    parent.appendChild(child);
    permissionDirective.mounted(child, { value: 'allowed:perm' });
    expect(parent.contains(child)).toBe(true);
  });
});
