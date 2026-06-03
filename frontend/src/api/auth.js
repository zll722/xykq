import request from '../utils/request';

export function loginApi(payload) {
  return request.post('/auth/login', payload);
}

export function sendRegisterCodeApi(payload) {
  return request.post('/auth/send-register-code', payload);
}

export function sendResetCodeApi(payload) {
  return request.post('/auth/send-reset-code', payload);
}

export function resetPasswordApi(payload) {
  return request.post('/auth/reset-password', payload);
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
