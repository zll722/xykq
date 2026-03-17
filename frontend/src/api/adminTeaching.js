import request from '../utils/request';

export function listClassesApi() {
  return request.get('/admin/classes');
}

export function createClassApi(payload) {
  return request.post('/admin/classes', payload);
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

export function deleteCourseApi(id) {
  return request.delete(`/admin/courses/${id}`);
}

export function listSchedulesApi() {
  return request.get('/admin/schedules');
}

export function createScheduleApi(payload) {
  return request.post('/admin/schedules', payload);
}

export function deleteScheduleApi(id) {
  return request.delete(`/admin/schedules/${id}`);
}
