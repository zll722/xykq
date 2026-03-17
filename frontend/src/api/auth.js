import request from '../utils/request';

export function loginApi(payload) {
  return request.post('/auth/login', payload);
}

export function meApi() {
  return request.get('/auth/me');
}

export function permissionsApi() {
  return request.get('/auth/permissions');
}
