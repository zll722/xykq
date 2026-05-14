import request from '../utils/request';

export function listAdminUsersApi(params) {
  return request.get('/admin/users', { params });
}

export function listAdminUserClassesApi() {
  return request.get('/admin/users/classes');
}

export function createAdminUserApi(payload) {
  return request.post('/admin/users', payload);
}

export function updateAdminUserApi(id, payload) {
  return request.put(`/admin/users/${id}`, payload);
}

export function updateAdminUserStatusApi(id, status) {
  return request.put(`/admin/users/${id}/status`, null, { params: { status } });
}

export function deleteAdminUserApi(id) {
  return request.delete(`/admin/users/${id}`);
}
