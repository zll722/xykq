import request from '../utils/request';

export function getMyProfileApi() {
  return request.get('/users/me');
}

export function updateMyProfileApi(payload) {
  return request.put('/users/me', payload);
}

export function changePasswordApi(payload) {
  return request.post('/auth/change-password', payload);
}
