import { defineStore } from 'pinia';
import { loginApi, meApi, permissionsApi } from '../api/auth';

export const useAuthStore = defineStore('auth', {
  state: () => ({
    token: localStorage.getItem('token') || '',
    userInfo: JSON.parse(localStorage.getItem('userInfo') || 'null'),
    permissions: JSON.parse(localStorage.getItem('permissions') || '[]')
  }),
  actions: {
    async login(username, password) {
      const data = await loginApi({ username, password });
      this.token = data.token;
      this.userInfo = data.userInfo;
      this.permissions = data.permissions || [];
      localStorage.setItem('token', data.token);
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo));
      localStorage.setItem('permissions', JSON.stringify(this.permissions));
    },
    async fetchMe() {
      this.userInfo = await meApi();
      localStorage.setItem('userInfo', JSON.stringify(this.userInfo));
      if (!this.permissions || this.permissions.length === 0) {
        this.permissions = await permissionsApi();
        localStorage.setItem('permissions', JSON.stringify(this.permissions));
      }
    },
    logout() {
      this.token = '';
      this.userInfo = null;
      this.permissions = [];
      localStorage.removeItem('token');
      localStorage.removeItem('userInfo');
      localStorage.removeItem('permissions');
    },
    hasPermission(code) {
      return this.permissions.includes(code);
    }
  }
});
