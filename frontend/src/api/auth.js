import request from '../utils/request';

export function loginApi(payload) {
  return request.post('/auth/login', payload);
}

export function registerApi(payload) {
  return request.post('/auth/register', payload);
}

export function getClassesApi() {
  return request.get('/auth/classes');
}

export function meApi() {
  return request.get('/auth/me');
}

export function permissionsApi() {
  return request.get('/auth/permissions');
}
