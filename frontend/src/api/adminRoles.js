import request from '../utils/request';

export function listRolesApi() {
  return request.get('/admin/roles');
}

export function createRoleApi(payload) {
  return request.post('/admin/roles', payload);
}

export function deleteRoleApi(id) {
  return request.delete(`/admin/roles/${id}`);
}

export function listAllPermissionsApi() {
  return request.get('/admin/roles/permissions/all');
}

export function listRolePermissionsApi(id) {
  return request.get(`/admin/roles/${id}/permissions`);
}

export function updateRolePermissionsApi(id, permissionIds) {
  return request.put(`/admin/roles/${id}/permissions`, { permissionIds });
}
