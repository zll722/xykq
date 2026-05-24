import request from '../utils/request';

export function listClassesApi() {
  return request.get('/admin/classes');
}

export function createClassApi(payload) {
  return request.post('/admin/classes', payload);
}

export function updateClassApi(id, payload) {
  return request.put(`/admin/classes/${id}`, payload);
}

export function deleteClassApi(id) {
  return request.delete(`/admin/classes/${id}`);
}

export function listCoursesApi() {
  return request.get('/admin/courses');
}

export function createCourseApi(payload) {
  return request.post('/admin/courses', payload);
}

export function updateCourseApi(id, payload) {
  return request.put(`/admin/courses/${id}`, payload);
}

export function deleteCourseApi(id) {
  return request.delete(`/admin/courses/${id}`);
}

/**
 * 按角色查用户列表，用于下拉选人
 * @param {string} roleCode - COUNSELOR | TEACHER
 */
export function listUsersByRoleApi(roleCode) {
  return request.get('/admin/users', { params: { roleCode } });
}

export function listSchedulesApi() {
  return request.get('/admin/schedules');
}

export function createScheduleApi(payload) {
  return request.post('/admin/schedules', payload);
}

export function updateScheduleApi(id, payload) {
  return request.put(`/admin/schedules/${id}`, payload);
}

export function deleteScheduleApi(id) {
  return request.delete(`/admin/schedules/${id}`);
}
